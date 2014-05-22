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

public class InputParameters extends AbstractList implements NdrObject {

    private final List parameters = new ArrayList();

    public Object get(int index) {
        return parameters.get(index);
    }

    public int size() {
        return parameters.size();
    }

    public Object set(int index, Object object) {
        Element element = (Element) object;
        element.setEmbedded(false);
        return parameters.set(index, element);
    }

    public void add(int index, Object object) {
        Element element = (Element) object;
        element.setEmbedded(false);
        parameters.add(index, element);
    }

    public Object remove(int index) {
        return parameters.remove(index);
    }

    public void read(NetworkDataRepresentation ndr) {
        Iterator parameters = iterator();
        List pipes = new ArrayList();
        boolean firstParameter = true;
        while (parameters.hasNext()) {
            Element parameter = (Element) parameters.next();
            if (parameter instanceof Pipe) {
                pipes.add(parameter);
                continue;
            }
            if (firstParameter) {
                ndr.getBuffer().align(8);
                firstParameter = false;
            }
            ndr.readElement(parameter);
        }
        if (pipes.isEmpty()) return;
        ndr.getBuffer().align(8);
        parameters = pipes.iterator();
        while (parameters.hasNext()) {
            ndr.readElement((Element) parameters.next());
        }
    }

    public void write(NetworkDataRepresentation ndr) {
        Iterator parameters = iterator();
        List pipes = new ArrayList();
        boolean firstParameter = true;
        while (parameters.hasNext()) {
            Element parameter = (Element) parameters.next();
            if (parameter instanceof Pipe) {
                pipes.add(parameter);
                continue;
            }
            if (firstParameter) {
                ndr.getBuffer().align(8, (byte) 0);
                firstParameter = false;
            }
            ndr.writeElement(parameter);
        }
        if (pipes.isEmpty()) return;
        ndr.getBuffer().align(8, (byte) 0);
        parameters = pipes.iterator();
        while (parameters.hasNext()) {
            ndr.writeElement((Element) parameters.next());
        }
    }

}
