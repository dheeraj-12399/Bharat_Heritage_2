# Bharat Heritage Backend Service

Production-ready Node.js backend providing:
- Real SMS delivery via Twilio or Fast2SMS
- Server-side cryptographically secure random 6-digit OTP generation
- HMAC-SHA256 salted hashing with 5-minute expiry
- Strict single-use OTP invalidation, rate-limiting, and 3-attempt lockout
- Gemini AI Heritage Assistant endpoint (zero client-side API key leakage)

## Quick Start

### 1. Install Dependencies
```bash
cd server
npm install
```

### 2. Configure Environment
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Provide your Twilio credentials or Fast2SMS API key.

### 3. Run Server
```bash
npm start
```

## API Endpoints

- `GET /api/health` - Service health status
- `POST /api/auth/send-otp` - Request OTP dispatch to phone (`{ "phoneNumber": "+91 9876543210" }`)
- `POST /api/auth/verify-otp` - Verify 6-digit OTP (`{ "phoneNumber": "+91 9876543210", "otp": "123456" }`)
- `POST /api/ai/chat` - Heritage AI question endpoint (`{ "message": "Tell me about Hampi" }`)
