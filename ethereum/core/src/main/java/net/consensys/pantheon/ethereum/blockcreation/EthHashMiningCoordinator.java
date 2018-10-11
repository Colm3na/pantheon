package net.consensys.pantheon.ethereum.blockcreation;

import net.consensys.pantheon.ethereum.chain.BlockAddedObserver;
import net.consensys.pantheon.ethereum.chain.Blockchain;
import net.consensys.pantheon.ethereum.core.Address;
import net.consensys.pantheon.ethereum.mainnet.EthHashSolution;
import net.consensys.pantheon.ethereum.mainnet.EthHashSolverInputs;

import java.util.Optional;

/**
 * Responsible for determining when a block mining operation should be started/stopped, then
 * creating an appropriate miner and starting it running in a thread.
 */
public class EthHashMiningCoordinator extends AbstractMiningCoordinator<Void, EthHashBlockMiner>
    implements BlockAddedObserver {

  private final EthHashMinerExecutor executor;
  private volatile Optional<Long> cachedHashesPerSecond = Optional.empty();

  public EthHashMiningCoordinator(
      final Blockchain blockchain, final EthHashMinerExecutor executor) {
    super(blockchain, executor);
    this.executor = executor;
  }

  @Override
  public void setCoinbase(final Address coinbase) {
    executor.setCoinbase(coinbase);
  }

  @Override
  public Optional<Address> getCoinbase() {
    return executor.getCoinbase();
  }

  @Override
  public Optional<Long> hashesPerSecond() {
    final Optional<Long> currentHashesPerSecond =
        currentRunningMiner.flatMap(EthHashBlockMiner::getHashesPerSecond);

    if (currentHashesPerSecond.isPresent()) {
      cachedHashesPerSecond = currentHashesPerSecond;
      return currentHashesPerSecond;
    } else {
      return cachedHashesPerSecond;
    }
  }

  @Override
  public Optional<EthHashSolverInputs> getWorkDefinition() {
    return currentRunningMiner.flatMap(EthHashBlockMiner::getWorkDefinition);
  }

  @Override
  public boolean submitWork(final EthHashSolution solution) {
    synchronized (this) {
      return currentRunningMiner.map(miner -> miner.submitWork(solution)).orElse(false);
    }
  }
}
