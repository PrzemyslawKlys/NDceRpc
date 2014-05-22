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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Structure extends AbstractList implements Element {

    private final List members = new ArrayList();

    private boolean embedded;

    public int getAlignment() {
        int maxAlign = 0;
        Iterator members = iterator();
        Element member;
        int alignment;
        while (members.hasNext()) {
            member = (Element) members.next();
            alignment = (member instanceof Union) ?
                    ((Union) member).getMaxAlignment() : member.getAlignment();
            if (alignment == 8) return 8;
            if (alignment > maxAlign) maxAlign = alignment;
        }
        return maxAlign;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public Object get(int index) {
        return members.get(index);
    }

    public int size() {
        return members.size();
    }

    public Object set(int index, Object object) {
        Element element = (Element) object;
        element.setEmbedded(true);
        return members.set(index, element);
    }

    public void add(int index, Object object) {
        Element element = (Element) object;
        element.setEmbedded(true);
        members.add(index, element);
    }

    public Object remove(int index) {
        Element element = (Element) members.remove(index);
        element.setEmbedded(false);
        return element;
    }

    public void read(NetworkDataRepresentation ndr) {
        Iterator members = iterator();
        while (members.hasNext()) ndr.readElement((Element) members.next());
    }

    public void write(NetworkDataRepresentation ndr) {
        Iterator members = iterator();
        while (members.hasNext()) ndr.writeElement((Element) members.next());
    }

    public Object clone() {
        try {
            Structure clone = (Structure) super.clone();
            ListIterator members = clone.members.listIterator();
            while (members.hasNext()) {
                members.set(((Element) members.next()).clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException();
        }
    }

}
