package it.unibo.ds.core.assets;

import com.owlike.genson.annotation.JsonProperty;
import it.unibo.ds.core.utils.Choice;
import it.unibo.ds.core.utils.Utils;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Ballot} implementation.
 */
@DataType()
public final class BallotImpl implements Ballot {

    @Property()
    private final String electionID;

    @Property()
    private final String voterCodeID;

    @Property()
    private final LocalDateTime date;

    @Property()
    private final Choice choice;

    @Override
    public String getElectionID() {
        return this.electionID;
    }

    @Override
    public String getVoterID() {
        return this.voterCodeID;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public Choice getChoice() {
        return this.choice;
    }

    private BallotImpl(@JsonProperty("electionID") final String electionID,
                       @JsonProperty("voterCodeID") final String voterCodeID,
                  @JsonProperty("date") final LocalDateTime date, @JsonProperty("choice") final Choice choice) {
        this.electionID = electionID;
        this.voterCodeID = voterCodeID;
        this.date = date;
        this.choice = choice;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Ballot other = (Ballot) obj;

        return getDate().equals(other.getDate())
                && getChoice().equals(other.getChoice())
                && Objects.deepEquals(
                        new String[] {getElectionID(), getVoterID()},
                        new String[] {other.getElectionID(), other.getVoterID()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElectionID(), getVoterID(), getDate(), getChoice());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [electionID="
                + this.electionID + ", voterID="
                + this.voterCodeID + ", date=" + this.date + ", choice=" + this.choice + "]";
    }

    /**
     * A {@link BallotBuilder}'s implementation.
     */
    public static final class Builder implements BallotBuilder {

        private Optional<String> electionId;
        private Optional<String> voterId;
        private Optional<LocalDateTime> date;
        private Optional<Choice> choice;

        private void check(final Object input) {
            Objects.requireNonNull(input);
        }
        private void checkString(final String input, final String inputValue) {
            if (input.equals("")) {
                throw new IllegalArgumentException("Invalid " + inputValue + ": " + input);
            }
        }

        private void checkDate(final LocalDateTime date) {
            if (date.isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Invalid date: " + date
                        + "\nRequired a date before now");
            }
        }

        private void checkDates(final LocalDateTime date, final LocalDateTime start, final LocalDateTime end) {
            if (!Utils.isDateBetween(date, start, end)) {
                throw new IllegalArgumentException("Invalid date: " + date
                        + "\nRequired a date after " + start + " and before " + end);
            }
        }

        private void checkChoice(final Choice choice, final List<Choice> choices) {
            if (!choices.contains(choice)) {
                throw new IllegalArgumentException("Choice expressed " + choice
                        + " is not in possible choices: " + choices);
            }
        }

        @Override
        public BallotBuilder electionID(final String electionID) {
            checkString(electionID, "electionID");
            this.electionId = Optional.of(electionID);
            return this;
        }

        @Override
        public BallotBuilder voterID(final String voterID) {
            checkString(voterID, "voterID");
            this.voterId = Optional.of(voterID);
            return this;
        }

        @Override
        public BallotBuilder dateUnchecked(final LocalDateTime date) {
            check(date);
            checkDate(date);
            this.date = Optional.of(date);
            return this;
        }

        @Override
        public BallotBuilder dateChecked(final LocalDateTime date, final LocalDateTime start,
                                         final LocalDateTime end) {
            check(date);
            check(start);
            check(end);
            checkDate(date);
            checkDates(date, start, end);
            this.date = Optional.of(date);
            return this;
        }

        @Override
        public BallotBuilder choiceUnchecked(final Choice choice) {
            check(choice);
            this.choice = Optional.of(choice);
            return this;
        }

        @Override
        public BallotBuilder choiceChecked(final Choice choice, final List<Choice> choices) {
            check(choice);
            check(choices);
            checkChoice(choice, choices);
            this.choice = Optional.of(choice);
            return this;
        }

        @Override
        public Ballot build() {
            return new BallotImpl(this.electionId.orElseThrow(),
                    this.voterId.orElseThrow(),
                    this.date.orElseThrow(),
                    this.choice.orElseThrow());
        }
    }
}
