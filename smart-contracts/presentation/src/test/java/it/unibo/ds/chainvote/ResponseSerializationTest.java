package it.unibo.ds.chainvote;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.BallotImpl;
import it.unibo.ds.chainvote.utils.Choice;
import it.unibo.ds.chainvote.utils.Pair;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ResponseSerializationTest {

    private final Genson genson = GensonUtils.create();

    @Test
    void testSimpleResult() {
        final Pair<Response<String>, String> expectedResult = getSimpleResult();
        final var serialized = genson.serialize(expectedResult.first());
        assertEquals(expectedResult.second(), serialized);
        final var deserialized = genson.deserialize(serialized, Response.class);
        assertEquals(expectedResult.first(), deserialized);
    }

    @Test
    void testComplexResult() {
        final Pair<Response<Ballot>, String> expectedResult = getComplexResult();
        final var serialized = genson.serialize(expectedResult.first());
        assertEquals(expectedResult.second(), serialized);
        final Response<Ballot> deserialized = genson.deserialize(serialized, new GenericType<>() { });
        assertEquals(expectedResult.first(), deserialized);
    }

    @Test
    void testErrorResult() {
        final Pair<Response<Object>, String> expectedResult = getErrorResult();
        final var serialized = genson.serialize(expectedResult.first());
        assertEquals(expectedResult.second(), serialized);
        final Response<Object> deserialized = genson.deserialize(serialized, new GenericType<>() { });
        assertEquals(expectedResult.first(), deserialized);
    }

    private Pair<Response<String>, String> getSimpleResult() {
        final Response<String> result = Response.success("simple-result");
        return new Pair<>(
            result,
            "{\"error\":null,\"result\":\"simple-result\",\"success\":true}"
        );
    }

    private Pair<Response<Ballot>, String> getComplexResult() {
        final Ballot ballot = new BallotImpl.Builder()
            .electionID("test-election-id")
            .voterID("test-voter-id")
            .date(LocalDateTime.parse("2023-08-20T10:00:00"))
            .choice(new Choice("yes"))
            .choice(new Choice("no"))
            .build();
        final Response<Ballot> result = Response.success(ballot);
        return new Pair<>(
            result,
            "{\"error\":null,\"result\":" + genson.serialize(ballot) + ",\"success\":true}"
        );
    }

    private Pair<Response<Object>, String> getErrorResult() {
        final Response<Object> result = Response.error("Error message test");
        return new Pair<>(
            result,
            "{\"error\":\"Error message test\",\"result\":null,\"success\":false}"
        );
    }
}
