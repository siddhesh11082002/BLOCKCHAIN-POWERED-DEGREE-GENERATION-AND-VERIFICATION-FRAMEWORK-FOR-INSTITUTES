package com.example.certificatesystem.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.3.
 */
@SuppressWarnings("rawtypes")
public class CertificateRegistry extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b5033600160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506108d9806100616000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c806316ab3cad1461003b578063c4b14e0b1461006b575b600080fd5b610055600480360381019061005091906103b8565b61009b565b604051610062919061042f565b60405180910390f35b6100856004803603810190610080919061044a565b610184565b60405161009291906104f6565b60405180910390f35b6000600160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161461012d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016101249061059b565b60405180910390fd5b81600080858152602001908152602001600020908161014c91906107d1565b50827f7020253f28647a3128f2234a7455840a694018548c4d862e24fbd3d82bd66e8d60405160405180910390a26001905092915050565b606060008083815260200190815260200160002080546101a3906105ea565b80601f01602080910402602001604051908101604052809291908181526020018280546101cf906105ea565b801561021c5780601f106101f15761010080835404028352916020019161021c565b820191906000526020600020905b8154815290600101906020018083116101ff57829003601f168201915b50505050509050919050565b6000604051905090565b600080fd5b600080fd5b6000819050919050565b61024f8161023c565b811461025a57600080fd5b50565b60008135905061026c81610246565b92915050565b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6102c58261027c565b810181811067ffffffffffffffff821117156102e4576102e361028d565b5b80604052505050565b60006102f7610228565b905061030382826102bc565b919050565b600067ffffffffffffffff8211156103235761032261028d565b5b61032c8261027c565b9050602081019050919050565b82818337600083830152505050565b600061035b61035684610308565b6102ed565b90508281526020810184848401111561037757610376610277565b5b610382848285610339565b509392505050565b600082601f83011261039f5761039e610272565b5b81356103af848260208601610348565b91505092915050565b600080604083850312156103cf576103ce610232565b5b60006103dd8582860161025d565b925050602083013567ffffffffffffffff8111156103fe576103fd610237565b5b61040a8582860161038a565b9150509250929050565b60008115159050919050565b61042981610414565b82525050565b60006020820190506104446000830184610420565b92915050565b6000602082840312156104605761045f610232565b5b600061046e8482850161025d565b91505092915050565b600081519050919050565b600082825260208201905092915050565b60005b838110156104b1578082015181840152602081019050610496565b60008484015250505050565b60006104c882610477565b6104d28185610482565b93506104e2818560208601610493565b6104eb8161027c565b840191505092915050565b6000602082019050818103600083015261051081846104bd565b905092915050565b600082825260208201905092915050565b7f4f6e6c79206f776e65722063616e2063616c6c20746869732066756e6374696f60008201527f6e00000000000000000000000000000000000000000000000000000000000000602082015250565b6000610585602183610518565b915061059082610529565b604082019050919050565b600060208201905081810360008301526105b481610578565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000600282049050600182168061060257607f821691505b602082108103610615576106146105bb565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b60006008830261067d7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610640565b6106878683610640565b95508019841693508086168417925050509392505050565b6000819050919050565b6000819050919050565b60006106ce6106c96106c48461069f565b6106a9565b61069f565b9050919050565b6000819050919050565b6106e8836106b3565b6106fc6106f4826106d5565b84845461064d565b825550505050565b600090565b610711610704565b61071c8184846106df565b505050565b5b8181101561074057610735600082610709565b600181019050610722565b5050565b601f821115610785576107568161061b565b61075f84610630565b8101602085101561076e578190505b61078261077a85610630565b830182610721565b50505b505050565b600082821c905092915050565b60006107a86000198460080261078a565b1980831691505092915050565b60006107c18383610797565b9150826002028217905092915050565b6107da82610477565b67ffffffffffffffff8111156107f3576107f261028d565b5b6107fd82546105ea565b610808828285610744565b600060209050601f83116001811461083b5760008415610829578287015190505b61083385826107b5565b86555061089b565b601f1984166108498661061b565b60005b828110156108715784890151825560018201915060208501945060208101905061084c565b8683101561088e578489015161088a601f891682610797565b8355505b6001600288020188555050505b50505050505056fea2646970667358221220f18450a4c177580c12cae9be7f3da4a56c0e9769ca362810efa61e8a1fabc48264736f6c63430008110033";

    private static String librariesLinkedBinary;

    public static final String FUNC_STORECERTIFICATE = "storeCertificate";

    public static final String FUNC_GETSIGNATURE = "getSignature";

    public static final Event CERTIFICATESTORED_EVENT = new Event("CertificateStored", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}));
    ;

    @Deprecated
    protected CertificateRegistry(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CertificateRegistry(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CertificateRegistry(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CertificateRegistry(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<CertificateStoredEventResponse> getCertificateStoredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CERTIFICATESTORED_EVENT, transactionReceipt);
        ArrayList<CertificateStoredEventResponse> responses = new ArrayList<CertificateStoredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CertificateStoredEventResponse typedResponse = new CertificateStoredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.dataHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CertificateStoredEventResponse getCertificateStoredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CERTIFICATESTORED_EVENT, log);
        CertificateStoredEventResponse typedResponse = new CertificateStoredEventResponse();
        typedResponse.log = log;
        typedResponse.dataHash = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<CertificateStoredEventResponse> certificateStoredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCertificateStoredEventFromLog(log));
    }

    public Flowable<CertificateStoredEventResponse> certificateStoredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CERTIFICATESTORED_EVENT));
        return certificateStoredEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> storeCertificate(byte[] dataHash,
            byte[] signature) {
        final Function function = new Function(
                FUNC_STORECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(dataHash), 
                new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> getSignature(byte[] dataHash) {
        final Function function = new Function(FUNC_GETSIGNATURE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(dataHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    @Deprecated
    public static CertificateRegistry load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CertificateRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CertificateRegistry load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CertificateRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CertificateRegistry load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CertificateRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CertificateRegistry load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CertificateRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CertificateRegistry> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CertificateRegistry.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<CertificateRegistry> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CertificateRegistry.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<CertificateRegistry> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CertificateRegistry.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<CertificateRegistry> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CertificateRegistry.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class CertificateStoredEventResponse extends BaseEventResponse {
        public byte[] dataHash;
    }
}
