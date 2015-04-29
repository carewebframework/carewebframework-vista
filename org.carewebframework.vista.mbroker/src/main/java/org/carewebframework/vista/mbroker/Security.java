/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.mbroker;

import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.common.StrUtil;

/**
 * Static class for security operations.
 */

public class Security {
    
    public enum AuthStatus {
        SUCCESS, EXPIRED, NOLOGINS, LOCKED, FAILURE, CANCELED;
        
        public boolean succeeded() {
            return this == SUCCESS || this == EXPIRED;
        }
    };
    
    /**
     * Result of an authorization request.
     */
    public static class AuthResult {
        
        public final AuthStatus status;
        
        public final String reason;
        
        public AuthResult(String data) {
            String[] pcs = StrUtil.split(data, StrUtil.U, 2);
            status = AuthStatus.values()[NumberUtils.toInt(pcs[0], AuthStatus.FAILURE.ordinal())];
            reason = pcs[1];
        }
    }
    
    /**
     * Validates the current user's password.
     *
     * @param session Broker session.
     * @param password Password.
     * @return True if the password is valid.
     */
    public static boolean validatePassword(BrokerSession session, String password) {
        password = encrypt(password, session.getServerCaps().getCipherKey());
        return session.callRPCBool("RGCWFUSR VALIDPSW", password);
    }
    
    /**
     * Change a password.
     *
     * @param session Broker session.
     * @param oldPassword Old password.
     * @param newPassword New password.
     * @return Status message from server.
     */
    public static String changePassword(BrokerSession session, String oldPassword, String newPassword) {
        String cipherKey = session.getServerCaps().getCipherKey();
        String result = session.callRPC("RGNETBRP CVC", encrypt(oldPassword, cipherKey), encrypt(newPassword, cipherKey));
        return result.startsWith("0") ? null : StrUtil.piece(result, StrUtil.U, 2);
    }
    
    /**
     * Encrypt a string value.
     *
     * @param value Value to encrypt.
     * @return Encrypted value.
     */
    protected static String encrypt(String value, String cipherKey) {
        String[] cipher = CipherRegistry.getCipher(cipherKey);
        int associatorIndex = randomIndex(cipher.length);
        int identifierIndex;
        
        do {
            identifierIndex = randomIndex(cipher.length);
        } while (associatorIndex == identifierIndex);
        
        return ((char) (associatorIndex + 32)) + StrUtil.xlate(value, cipher[associatorIndex], cipher[identifierIndex])
                + ((char) (identifierIndex + 32));
    }
    
    /**
     * Decrypt a string value.
     *
     * @param value Value to decrypt.
     * @return Decrypted value.
     */
    protected static final String decrypt(String value, String cipherKey) {
        int len = value == null ? 0 : value.length();
        
        if (len < 3) {
            return "";
        }
        
        int identifierIndex = value.charAt(0) - 32;
        int associatorIndex = value.charAt(len - 1) - 32;
        String[] cipher = CipherRegistry.getCipher(cipherKey);
        
        return StrUtil.xlate(value.substring(1, len - 1), cipher[associatorIndex], cipher[identifierIndex]);
    }
    
    /**
     * Return random index into cipher table.
     * 
     * @param maxKeys Maximum number of keys.
     * @return Cipher table index.
     */
    private static int randomIndex(int maxKeys) {
        return (int) Math.floor(Math.random() * maxKeys);
    }
    
    /**
     * Enforce singleton instance.
     */
    private Security() {
    }
}
