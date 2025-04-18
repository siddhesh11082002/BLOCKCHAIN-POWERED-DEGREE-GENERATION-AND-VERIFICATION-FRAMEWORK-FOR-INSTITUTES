package com.example.certificatesystem.service;
import com.example.certificatesystem.contract.CertificateRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@Slf4j
public class BlockchainService {
    private final Web3j web3j;
    private CertificateRegistry contract;
    
    @Value("${blockchain.contract.address}")
    private String contractAddress;
    
    @Value("${blockchain.account.privateKey}")
    private String privateKey;
    
    @Autowired
    public BlockchainService(Web3j web3j) {
        this.web3j = web3j;
        log.info("BlockchainService initialized with Web3j");
    }
    
    @PostConstruct
    public void init() throws Exception {
        try {
            Credentials credentials = Credentials.create(privateKey);
            ContractGasProvider gasProvider = new DefaultGasProvider();
            
            this.contract = CertificateRegistry.load(
                contractAddress, 
                web3j, 
                credentials, 
                gasProvider
            );
            
            log.info("Connected to CertificateRegistry contract at address: {}", contractAddress);
        } catch (Exception e) {
            log.error("Failed to initialize blockchain connection", e);
            throw e;
        }
    }
    
    public String storeCertificate(byte[] dataHash, byte[] signature) throws Exception {
        try {
            log.info("Storing certificate on blockchain, hash: {}", Arrays.toString(dataHash));
            
            // Ensure the hash is exactly 32 bytes
            byte[] hash32Bytes = new byte[32];
            System.arraycopy(dataHash, 0, hash32Bytes, 0, Math.min(dataHash.length, 32));
            
            // Pass byte arrays directly to the method
            TransactionReceipt receipt = contract.storeCertificate(hash32Bytes, signature).send();
            
            String txHash = receipt.getTransactionHash();
            log.info("Certificate stored on blockchain, tx hash: {}", txHash);
            return txHash;
        } catch (Exception e) {
            log.error("Failed to store certificate on blockchain", e);
            throw e;
        }
    }
    
    public byte[] getSignature(byte[] dataHash) throws Exception {
        try {
            log.info("Retrieving signature from blockchain for hash: {}", Arrays.toString(dataHash));
            
            // Ensure the hash is exactly 32 bytes
            byte[] hash32Bytes = new byte[32];
            System.arraycopy(dataHash, 0, hash32Bytes, 0, Math.min(dataHash.length, 32));
            
            // Pass byte array directly to the method
            byte[] signatureValue = contract.getSignature(hash32Bytes).send();
            
            if (signatureValue == null || signatureValue.length == 0) {
                log.warn("No signature found for hash");
                return new byte[0];
            }
            
            log.info("Retrieved signature from blockchain");
            return signatureValue;
        } catch (Exception e) {
            log.error("Failed to retrieve signature from blockchain", e);
            throw e;
        }
    }
}