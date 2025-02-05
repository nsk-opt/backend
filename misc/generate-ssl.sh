#!/bin/bash

SSL_PRIVATE_DIR="/app/ssl/private"
SSL_CERTS_DIR="/app/ssl/certs"

KEY_FILE="$SSL_PRIVATE_DIR/private.key"
CERT_FILE="$SSL_CERTS_DIR/certificate.crt"
COMBINED_FILE="/tmp/combined.pem"

cat $CERT_FILE $KEY_FILE >$COMBINED_FILE

openssl pkcs12 -export -in $COMBINED_FILE -out /app/src/main/resources/keystore.p12 -name server -password pass:$SSL_KEY_PASSWORD -noiter -nomaciter
