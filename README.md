# SEP Payment System

The system consists of the following main services:

* **Web Shop Frontend** – customer and admin UI for browsing vehicles, creating reservations, and viewing reservation history.
* **Web Shop Backend** – handles authentication, vehicle and reservation management, and communication with the PSP.
* **PSP Frontend** – UI for payment method selection, merchant account management, and plugin management.
* **PSP Backend** – central payment service provider system responsible for merchant management, plugin management, payment transaction tracking, and payment result notifications.
* **Bank Payment Plugin** – payment plugin used by the PSP for card and QR payments.
* **Bank Backend** – mock bank/acquirer system that processes card and QR payments.

## Web-shop functionality

The web-shop allows customers to browse available vehicles, view vehicle details, select reservation dates, choose additional options, and create reservations. After a reservation is created, the customer is redirected to the PSP payment page.

The web-shop also supports user authentication, admin vehicle/reservation management, and reservation history. Reservation history includes payment-related information such as total price, payment method, and payment status.

## Payment functionality

The payment system supports card payments and IPS QR payments. The PSP is responsible for creating and tracking payment transactions, displaying available payment methods, initiating payments through the appropriate plugin, and receiving final payment results.

Payment results are sent back through the plugin and PSP, and the web-shop is notified asynchronously through RabbitMQ. The system handles successful payments, failed payments, unavailable payment services, abandoned payments, and expired payment attempts.

## Plugin functionality

Payment methods are integrated through independent plugins. Each plugin can define its own supported payment methods, configuration fields, and communication logic, while the PSP uses a common plugin interface to register, configure, and initiate payments.

## Running the project

Before running the services, local HTTPS certificates and Docker containers must be prepared.

### HTTPS certificates

Local HTTPS certificates and truststores are generated using PowerShell scripts located in: ```certs-scripts/```

To generate all required keystores, truststores, and Nginx certificates, run:

```powershell
cd certs-scripts
.\setup-all-certs.ps1
```

The script creates:

```text
keystore.p12
truststore.p12
nginx.crt
nginx.key
```

If PowerShell blocks script execution, run:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass -Force
```

### Docker containers

The project uses Docker containers for infrastructure services such as databases, RabbitMQ, pgAdmin, and the Nginx image server.

Docker configuration is located in: ```docker/```

To start the required containers, run:

```bash
cd docker
docker compose up -d
```

### Spring Boot services

The backend services are Spring Boot applications:

```text
web-shop/back
psp/back
psp/plugin/bank-payment-plugin
bank/back
```

Each Spring Boot service requires a local `.env` file in its project root. The `.env` file contains service-specific configuration such as database connection settings, ports, URLs, secrets, and HTTPS settings.

Services can be started from the IDE or with Maven:

```bash
mvn spring-boot:run
```

For services that call other HTTPS services, the JVM truststore must be configured. In IntelliJ, add the following VM options to the run configuration:

```text
-Djavax.net.ssl.trustStore=$PROJECT_DIR$/src/main/resources/certs/truststore.p12
-Djavax.net.ssl.trustStorePassword=changeit
-Djavax.net.ssl.trustStoreType=PKCS12
```

### Frontend applications

The frontend applications are Next.js projects:

```text
web-shop/front
psp/front
```

Each frontend requires a local `.env.local` file in its project root. The file contains frontend-specific public URLs, such as backend API URLs and image server URL.

To run a frontend application:

```bash
npm install
npm run dev
```

## Service ports

The ports listed below are the default local development ports used in the provided `.env.example` files. They can be changed if needed, but all related service URLs must be updated consistently.

| Service             | URL                      |
| ------------------- | ------------------------ |
| Web Shop Frontend   | `https://localhost:3001` |
| PSP Frontend        | `https://localhost:3000` |
| Web Shop Backend    | `https://localhost:5101` |
| PSP Backend         | `https://localhost:5102` |
| Bank Payment Plugin | `https://localhost:5103` |
| Bank Backend        | `https://localhost:5104` |
| Nginx Image Server  | `https://localhost:5105` |

## Payment flow overview

The main payment flow is:

```text
Web Shop → PSP → Payment Plugin → Bank → Payment Plugin → PSP → Web Shop
```

The web-shop creates a reservation and sends a payment request to the PSP. The PSP creates a payment transaction and redirects the user to the PSP payment page. After the user selects a payment method, the PSP initiates the payment through the selected plugin.

The payment plugin communicates with the external payment system and returns a payment page URL. After the payment is completed, the result is sent back through the plugin and PSP. The web-shop receives the final payment result asynchronously through RabbitMQ and updates the reservation status.

## Security notes

REST communication between frontend, backend services, plugins, and the bank system is secured with HTTPS.

Service-to-service HTTPS communication uses local keystores and truststores. Each service has its own `keystore.p12`, while `truststore.p12` contains certificates of services that the current service needs to trust.

PSP-plugin communication is additionally protected using HMAC signatures. Plugin secrets are stored encrypted on the PSP side.

Card data is not stored in the system and is not written to logs. Sensitive local files such as `.env`, generated certificates, truststores, private keys, and logs are excluded from Git.

