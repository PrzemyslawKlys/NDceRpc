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

package rpc;

import java.util.Properties;

public abstract class StubFactory {

    public static StubFactory newInstance() {
        String instanceClass = System.getProperty("rpc.stubFactory");
        if (instanceClass == null) return new DefaultStubFactory();
        try {
            return (StubFactory) Class.forName(instanceClass).newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create stub factory: " +
                    ex);
        }
    }

    public abstract Stub createStub(Class interfaceClass)
            throws ProviderException;

    public abstract Stub createStub(String address, Class interfaceClass)
            throws ProviderException;

    public abstract Stub createStub(String address, Properties properties,
            Class interfaceClass) throws ProviderException;

    private static class DefaultStubFactory extends StubFactory {

        public Stub createStub(Class interfaceClass) throws ProviderException {
            return createStub(null, null, interfaceClass);
        }

        public Stub createStub(String address, Class interfaceClass)
                throws ProviderException {
            return createStub(address, null, interfaceClass);
        }

        public Stub createStub(String address, Properties properties,
                Class interfaceClass) throws ProviderException {
            try {
                Class stubClass = Class.forName(interfaceClass.getName() +
                        "_Stub");
                Stub stub = (Stub) stubClass.newInstance();
                stub.setAddress(address);
                stub.setProperties(properties);
                return stub;
            } catch (Exception ex) {
                throw new ProviderException("Unable to create stub: " + ex);
            }
        }

    }

}
