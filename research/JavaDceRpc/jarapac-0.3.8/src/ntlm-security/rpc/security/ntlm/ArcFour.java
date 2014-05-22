/* Jarapac DCE/RPC Framework
 * Copyright (C) 2003  Eric Glass
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package rpc.security.ntlm;

import java.security.Key;

import javax.crypto.Cipher;

import javax.crypto.spec.SecretKeySpec;

public class ArcFour {

    private final Key key;

    private final boolean reset;

    private final Cipher cipher;

    public ArcFour(byte[] key, boolean reset) {
        this.key = new SecretKeySpec(key, "ARC4");
        cipher = createCipher(this.key);
        this.reset = reset;
    }

    public void process(byte[] data, int offset, int length, byte[] output,
            int index) {
        try {
            cipher.update(data, offset, length, output, index);
            if (reset) cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception ex) {
            throw new IllegalStateException();
        }
    }

    private static Cipher createCipher(Key key) {
        try {
            Cipher cipher = Cipher.getInstance("ARC4");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create cipher: " + ex);
        }
    }

}
