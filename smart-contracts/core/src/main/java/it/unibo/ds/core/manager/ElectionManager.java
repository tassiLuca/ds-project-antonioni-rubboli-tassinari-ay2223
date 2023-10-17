package it.unibo.ds.core.manager;

import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionInfo;

/**
 * An interface modeling an {@link Election} manager.
 */
public interface ElectionManager {

    /**
     * TODO document.
     * @param election TODO
     * @param electionInfo TODO
     * @param ballot TODO
     */
    void castVote(Election election, ElectionInfo electionInfo, Ballot ballot);
}
