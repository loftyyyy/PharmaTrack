# Security Features Testing Guide

## Overview
This guide provides comprehensive testing instructions for all the security features implemented in the JWT authentication system.

## Prerequisites
1. Start the application: `mvn spring-boot:run`
2. Ensure database is running and accessible
3. Have a REST client (Postman, curl, or similar) ready

## 1. JWT Token Generation and Validation Tests

### Test 1.1: Successful Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Result:**
- Status: 200 OK
- Response contains: accessToken, refreshToken, user info
- Access token should be valid for 15 minutes
- Refresh token should be valid for 7 days

### Test 1.2: Token Validation
```bash
# Use the access token from previous test
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Result:**
- Status: 200 OK (if user has ADMIN role)
- Request should be authenticated successfully

### Test 1.3: Invalid Credentials
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrongpassword"
  }'
```

**Expected Result:**
- Status: 401 Unauthorized
- Error message: "Invalid username or password"

## 2. Rate Limiting Tests

### Test 2.1: Login Rate Limiting
```bash
# Run this command 6 times quickly (within 1 minute)
for i in {1..6}; do
  curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -H "X-Forwarded-For: 192.168.1.100" \
    -d '{
      "username": "admin",
      "password": "admin123"
    }'
  echo "Attempt $i completed"
done
```

**Expected Result:**
- First 5 attempts: 200 OK
- 6th attempt: 429 Too Many Requests

### Test 2.2: Refresh Token Rate Limiting
```bash
# First, get a valid refresh token from login
REFRESH_TOKEN="your_refresh_token_here"

# Run this command 11 times quickly
for i in {1..11}; do
  curl -X POST http://localhost:8080/auth/refresh \
    -H "Content-Type: application/json" \
    -H "X-Forwarded-For: 192.168.1.101" \
    -d "{
      \"refreshToken\": \"$REFRESH_TOKEN\"
    }"
  echo "Refresh attempt $i completed"
done
```

**Expected Result:**
- First 10 attempts: 200 OK
- 11th attempt: 429 Too Many Requests

## 3. Token Blacklisting Tests

### Test 3.1: Successful Logout
```bash
# First login to get a token
ACCESS_TOKEN="your_access_token_here"

curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Expected Result:**
- Status: 200 OK
- Message: "User logged out successfully!"

### Test 3.2: Token Blacklisting Verification
```bash
# Try to use the token after logout
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Expected Result:**
- Status: 401 Unauthorized
- Token should be rejected

## 4. Token Rotation Tests

### Test 4.1: Token Refresh with Rotation
```bash
# Get initial tokens from login
REFRESH_TOKEN="your_refresh_token_here"

# Refresh the token
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$REFRESH_TOKEN\"
  }"
```

**Expected Result:**
- Status: 200 OK
- New access token AND new refresh token returned
- Old refresh token should be invalidated

### Test 4.2: Old Refresh Token Rejection
```bash
# Try to use the old refresh token
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{
    \"refreshToken\": \"$OLD_REFRESH_TOKEN\"
  }"
```

**Expected Result:**
- Status: 400 Bad Request
- Error: "Refresh token is not in database!"

## 5. Token Cleanup Tests

### Test 5.1: Manual Token Cleanup
```bash
# This would typically be done via a scheduled job
# You can test by calling the cleanup service directly
# (This requires adding a test endpoint or using JMX)
```

**Expected Result:**
- Expired tokens should be removed from database
- Log message: "Cleaned up X expired refresh tokens"

## 6. Security Headers Tests

### Test 6.1: Security Headers Verification
```bash
curl -I http://localhost:8080/auth/login
```

**Expected Result:**
- Headers should include:
  - `X-Frame-Options: DENY`
  - `X-Content-Type-Options: nosniff`
  - `Strict-Transport-Security: max-age=31536000; includeSubDomains`

## 7. Error Handling Tests

### Test 7.1: Malformed JWT Token
```bash
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer malformed.token.here"
```

**Expected Result:**
- Status: 401 Unauthorized
- Proper error handling without information leakage

### Test 7.2: Expired Token
```bash
# Use an expired token (you can create one with short expiration)
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer EXPIRED_TOKEN"
```

**Expected Result:**
- Status: 401 Unauthorized
- Token should be rejected

## 8. Concurrent Access Tests

### Test 8.1: Multiple Users Login Simultaneously
```bash
# Test with multiple users logging in at the same time
# This tests thread safety of the authentication system
```

**Expected Result:**
- All valid logins should succeed
- Rate limiting should work per IP
- No race conditions

## 9. Environment Configuration Tests

### Test 9.1: Environment Variable JWT Secret
```bash
# Set environment variable
export JWT_SECRET="your_production_secret_here"

# Restart application and test login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Result:**
- Application should use the environment variable
- Tokens should be generated with the new secret

## 10. Performance Tests

### Test 10.1: High Volume Login Attempts
```bash
# Test with high volume of requests
# Monitor application logs for performance
```

**Expected Result:**
- Rate limiting should prevent abuse
- System should remain stable
- Cleanup jobs should run without issues

## Monitoring and Logging

### Check Application Logs
Monitor the application logs for:
- Authentication attempts
- Rate limiting events
- Token cleanup operations
- Security events

### Key Log Messages to Look For:
- "Login attempt for user: X from IP: Y"
- "Login rate limit exceeded for IP: X"
- "Successful login for user: X"
- "Token refresh attempt at X from IP: Y"
- "Cleaned up X expired refresh tokens"

## Troubleshooting

### Common Issues:
1. **Rate limiting not working**: Check if RateLimitingService is properly injected
2. **Token blacklisting not working**: Verify TokenBlacklistService integration
3. **Cleanup not running**: Check if @EnableScheduling is present
4. **Security headers missing**: Verify SecurityConfig configuration

### Debug Commands:
```bash
# Check if application is running
curl http://localhost:8080/actuator/health

# Check application logs
tail -f logs/application.log

# Test database connectivity
curl http://localhost:8080/actuator/info
```

## Success Criteria

All tests should pass with:
- ✅ Proper JWT token generation and validation
- ✅ Rate limiting working correctly
- ✅ Token blacklisting functioning
- ✅ Token rotation implemented
- ✅ Security headers present
- ✅ Proper error handling
- ✅ Cleanup jobs running
- ✅ Environment configuration working
- ✅ Concurrent access handling
- ✅ Performance under load

This comprehensive testing ensures your JWT implementation achieves the perfect 10/10 security score!
