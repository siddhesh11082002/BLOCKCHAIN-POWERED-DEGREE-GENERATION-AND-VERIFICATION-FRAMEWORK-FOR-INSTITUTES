# Blockchain-Based Certificate Verification System

A secure and efficient system for issuing, managing, and verifying academic certificates using blockchain technology, IPFS storage, and Merkle trees.

---

## 📌 Overview

This application provides a complete solution for educational institutions to issue tamper-proof digital certificates and for third parties to verify their authenticity. The system utilizes a dual-storage approach with Ethereum blockchain and IPFS to ensure security while minimizing gas costs.

---

## ✅ Features

- **Secure Certificate Issuance**: Generate and digitally sign certificates using RSA-2048
- **Blockchain Integration**: Store certificate signatures on Ethereum blockchain
- **IPFS Integration**: Store certificate packages on decentralized storage
- **Student Management**: Import and manage student records
- **PDF Generation**: Create professional certificate PDFs with embedded QR codes
- **Email Delivery**: Send certificates directly to students
- **Verification Portal**: Simple interface for third parties to verify certificates

---

## ⚙️ Prerequisites

- Java 11 or later  
- Node.js 14 or later  
- PostgreSQL 12 or later  
- Ganache for local Ethereum blockchain (or preferred testnet)  
- MetaMask browser extension  

---

## 🛠 Installation

### 🔧 Backend Setup

```bash
cd certificate-verification-system
1. Configure database connection in `src/main/resources/application.properties`:
```
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/certificate_system
spring.datasource.username=postgres
spring.datasource.password=yourpassword
```

2. Configure blockchain connection in `application.properties`:

```properties
blockchain.url=http://localhost:7545
blockchain.contract.address=0x…
blockchain.account.privateKey=0x...
```

3. Configure Pinata IPFS credentials in `application.properties`:

```properties
ipfs.api.key=your_pinata_api_key
ipfs.api.secret=your_pinata_secret_key
ipfs.jwt=your_pinata_jwt
```

4. Build the project:

```bash
./mvnw clean package
```

### Frontend Setup

1. Navigate to the frontend directory:

```bash
cd frontend
```

2. Install dependencies:

```bash
npm install
```

3. Configure API endpoint in `.env`:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

4. Start the development server:

```bash
npm run dev
```

## Smart Contract Deployment

1. Open the contract file `CertificateRegistry.sol` (provided in backend folder) in Remix IDE  
2. Compile the smart contract using Solidity Compiler in Remix  
3. Connect Remix IDE with your MetaMask wallet  
4. Deploy the smart contract:
   - For development: connect MetaMask to Ganache (`http://localhost:7545`)
   - For production: connect to an Ethereum testnet or mainnet  
5. Copy the deployed contract address  
6. Update the contract address in `application.properties`  

## Usage

### Certificate Issuance

1. Log in to the administrative interface  
2. Import students via Excel file or manual entry  
3. Queue students for certificate generation  
4. Generate certificates individually or in batches  
5. Certificates will be stored on IPFS and blockchain, then delivered to students  

### Certificate Verification

1. Access the verification portal  
2. Upload the certificate PDF  
3. The system will verify the certificate's authenticity using:
   - Digital signature verification with RSA  
   - Blockchain signature validation  
4. View verification results  

## Technologies Used

- **Frontend**: React.js, Axios, React Router  
- **Backend**: Java, Spring Boot, JPA/Hibernate  
- **Blockchain**: Ethereum, Solidity, Web3j, Ganache  
- **Storage**: PostgreSQL, IPFS, Pinata Cloud API  
- **Security**: RSA-2048, SHA-256  
- **Document Processing**: Apache PDFBox, ZXing (QR codes)  
- **Email**: JavaMail API, SMTP  

## Dependencies

### Backend Dependencies

- Spring Boot Starter Web  
- Spring Boot Starter Data JPA  
- Spring Boot Starter Security  
- PostgreSQL Driver  
- Web3j  
- Apache PDFBox  
- ZXing Core  
- ZXing JavaSE  
- JavaMail API  
- JSON  
- Lombok  
- Apache POI (Excel processing)  

### Frontend Dependencies

- React  
- React Router  
- Axios  
- React QR Reader

