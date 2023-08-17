package it.unibo.ds.chainvote.contract;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.assets.OneTimeCodeAsset;
import it.unibo.ds.chainvote.presentation.GensonUtils;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static it.unibo.ds.chainvote.contract.CodeManagerContract.CODES_COLLECTION;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class CodeManagerContractTest {

    private static final byte[] ELECTION_ID = "test-election".getBytes();
    private static final byte[] USER_ID = "mrossi".getBytes();
    private static final String KEY =
        new CompositeKey(Arrays.toString(ELECTION_ID), Arrays.toString(USER_ID)).getObjectType();

    private final Genson genson = GensonUtils.create();
    private final CodeManagerContract contract = new CodeManagerContract();
    private Context context;
    private ChaincodeStub stub;

    @BeforeEach
    void setup() {
        context = mock(Context.class);
        stub = mock(ChaincodeStub.class);
        when(context.getStub()).thenReturn(stub);
        assertEquals(stub, context.getStub());
    }

    @Nested
    class TestCodeGeneration {

        private final Map<String, byte[]> transientData = new HashMap<>() {{
            put("userId", USER_ID);
            put("electionId", ELECTION_ID);
        }};

        @Test
        void whenNotAlreadyRequested() {
            when(stub.getTransient()).thenReturn(transientData);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(new byte[0]);
            final OneTimeCodeAsset asset = contract.generateFor(context);
            assertNotNull(asset.getAsset().getCode());
            verify(stub).putPrivateData(CODES_COLLECTION, KEY, genson.serialize(asset));
        }

        @Test
        void whenAlreadyExists() {
            when(stub.getTransient()).thenReturn(transientData);
            final byte[] mockedCode = genson.serialize(new OneTimeCodeAsset(new OneTimeCodeImpl(0L))).getBytes();
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(mockedCode);
            final Throwable thrown = catchThrowable(() -> contract.generateFor(context));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("A one-time-code for the given election and user has already been generated");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ALREADY_GENERATED_CODE".getBytes());
        }

        @Test
        void whenTransientInputNotExists() {
            final Map<String, byte[]> incompleteTransientData = new HashMap<>() {{ put("userId", USER_ID); }};
            when(stub.getTransient()).thenReturn(incompleteTransientData);
            when(stub.getPrivateData(CODES_COLLECTION, KEY)).thenReturn(new byte[0]);
            final Throwable thrown = catchThrowable(() -> contract.generateFor(context));
            assertThat(thrown)
                .isInstanceOf(ChaincodeException.class)
                .hasMessage("A `electionId` transient input was expected.");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("INCOMPLETE_INPUT".getBytes());
        }
    }
}
