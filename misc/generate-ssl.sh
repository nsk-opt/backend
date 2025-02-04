#!/bin/bash

SSL_PRIVATE_DIR="/app/ssl/private"
SSL_CERTS_DIR="/app/ssl/certs"

KEY_FILE="$SSL_PRIVATE_DIR/private.key"
CERT_FILE="$SSL_CERTS_DIR/certificate.crt"
P12_FILE="/app/src/main/resources/keystore.p12"

openssl req -x509 -newkey rsa:2048 -keyout $KEY_FILE -out $CERT_FILE -days 365 -nodes -subj "/CN=${SERVER_DOMAIN}" >/dev/null 2>&1

openssl pkcs12 -export -in $CERT_FILE -inkey $KEY_FILE -out $P12_FILE -name server -password pass:$SSL_KEY_PASSWORD >/dev/null 2>&1

chmod 664 $P12_FILE
