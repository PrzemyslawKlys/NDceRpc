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

public class ElementHelper implements ArrayHelper {

    private final Element templateObject;

    public ElementHelper(Element templateObject) {
        this.templateObject = templateObject;
        if (templateObject == null) {
            throw new NullPointerException("Null template.");
        }
    }

    public Class getType() {
        return templateObject.getClass();
    }

    public int getAlignment() {
        return templateObject.getAlignment();
    }

    public Element getTemplate() {
        return templateObject;
    }

    public void readArray(Object array, int offset, int length,
            NetworkDataRepresentation ndr) {
        if (array == null) return;
        Element[] elementArray = (Element[]) array;
        length += offset;
        for (int i = offset; i < length; i++) {
            elementArray[i] = (Element) templateObject.clone();
            ndr.readElement(elementArray[i]);
        }
    }

    public void writeArray(Object array, int offset, int length,
            NetworkDataRepresentation ndr) {
        if (array == null) return;
        Element[] elementArray = (Element[]) array;
        length += offset;
        for (int i = offset; i < length; i++) {
            ndr.writeElement(elementArray[i]);
        }
    }

}
