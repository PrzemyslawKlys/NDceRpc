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

package rpc.ndr;

import java.lang.reflect.Array;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pipe extends AbstractList implements Element {

    private final List chunks = new ArrayList();

    private ArrayHelper helper;

    private boolean embedded;

    public Pipe(ArrayHelper helper) {
        setHelper(helper);
    }

    public ArrayHelper getHelper() {
        return helper;
    }

    public void setHelper(ArrayHelper helper) {
        if (helper == null) throw new NullPointerException("Null helper.");
        this.helper = helper;
    }

    public int getAlignment() {
        return getHelper().getAlignment();
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public Object get(int index) {
        return chunks.get(index);
    }

    public int size() {
        return chunks.size();
    }

    public Object set(int index, Object object) {
        if (Array.getLength(object) <= 0) {
            throw new IllegalArgumentException("Zero-length chunk prohibited.");
        }
        if (!getHelper().getType().isAssignableFrom(
                object.getClass().getComponentType())) {
            throw new ClassCastException("Chunk is not class compatible.");
        }
        return chunks.set(index, object);
    }

    public void add(int index, Object object) {
        if (Array.getLength(object) <= 0) {
            throw new IllegalArgumentException("Zero-length chunk prohibited.");
        }
        if (!getHelper().getType().isAssignableFrom(
                object.getClass().getComponentType())) {
            throw new ClassCastException("Chunk is not class compatible.");
        }
        chunks.add(index, object);
    }

    public Object remove(int index) {
        return chunks.remove(index);
    }

    public void read(NetworkDataRepresentation ndr) {
        clear();
        ArrayHelper helper = getHelper();
        Class objectClass = helper.getType();
        int count = 0;
        while ((count = (int) ndr.readUnsignedLong()) > 0) {
            Object chunk = Array.newInstance(objectClass, count);
            helper.readArray(chunk, 0, count, ndr);
            add(chunk);
        }
    }

    public void write(NetworkDataRepresentation ndr) {
        Iterator chunks = iterator();
        int count;
        while (chunks.hasNext()) {
            Object chunk = chunks.next();
            ndr.writeUnsignedLong(count = Array.getLength(chunk));
            helper.writeArray(chunk, 0, count, ndr);
        }
        ndr.writeUnsignedLong(0);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }
    }

}
