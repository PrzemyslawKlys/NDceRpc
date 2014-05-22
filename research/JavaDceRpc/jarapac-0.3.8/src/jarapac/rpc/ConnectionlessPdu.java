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

import rpc.core.PresentationSyntax;
import rpc.core.UUID;

import ndr.*;

public abstract class ConnectionlessPdu extends NdrObject implements ProtocolDataUnit {

    public static final int CONNECTIONLESS_MAJOR_VERSION = 4;

    public static final int MUST_RECEIVE_FRAGMENT_SIZE = 1464;

    public static final int AUTHENTICATION_PROTOCOL_NONE = 0;

    public static final int AUTHENTICATION_PROTOCOL_OSF_PRIVATE_KEY = 1;

    /**
     * Bidirectional flag indicating the PDU is the last fragment of
     * a multi-fragment transmission.
     */
    public static final int PFC1_LAST_FRAG = 0x02;

    /**
     * Bidirectional flag indicating the PDU is a fragment in
     * a multi-fragment transmission.
     */
    public static final int PFC1_FRAG = 0x04;

    /**
     * Bidirectional flag indicating the receiver is not required to send
     * a fragment acknowledge PDU.
     */
    public static final int PFC1_NOFACK = 0x08;

    /**
     * Client-to-server flag indicating the PDU is part of a "maybe" request.
     */
    public static final int PFC1_MAYBE = 0x10;

    /**
     * Client-to-server flag indicating the PDU is part of an idempotent
     * request.
     */
    public static final int PFC1_IDEMPOTENT = 0x20;

    /**
     * Client-to-server flag indicating the PDU is part of a broadcast
     * request.
     */
    public static final int PFC1_BROADCAST = 0x40;

    /**
     * Flag indicating a cancel is pending.
     */
    public static final int PFC2_CANCEL_PENDING = 0x02;

    public static final int MAJOR_VERSION_OFFSET = 0;

    public static final int TYPE_OFFSET = 1;

    public static final int FLAGS1_OFFSET = 2;

    public static final int FLAGS2_OFFSET = 3;

    public static final int DATA_REPRESENTATION_OFFSET = 4;

    public static final int SERIAL_HIGH_OFFSET = 7;

    public static final int OBJECT_OFFSET = 8;

    public static final int INTERFACE_OFFSET = 24;

    public static final int ACTIVITY_OFFSET = 40;

    public static final int SERVER_BOOT_OFFSET = 56;

    public static final int INTERFACE_VERSION_OFFSET = 60;

    public static final int SEQUENCE_NUMBER_OFFSET = 64;

    public static final int OPNUM_OFFSET = 68;

    public static final int INTERFACE_HINT_OFFSET = 70;

    public static final int ACTIVITY_HINT_OFFSET = 72;

    public static final int BODY_LENGTH_OFFSET = 74;

    public static final int FRAGMENT_NUMBER_OFFSET = 76;

    public static final int AUTHENTICATION_PROTOCOL_OFFSET = 78;

    public static final int SERIAL_LOW_OFFSET = 79;

    public static final int HEADER_LENGTH = 80;

    private Format format;

    private int flags1 = 0;

    private int flags2 = 0;

    private int serialNumber = 0;

    private UUID object;

    private PresentationSyntax syntax;

    private UUID activity;

    private int serverBootTime = 0;

    private int sequenceNumber = 0;

    private int opnum = 0;

    private int interfaceHint = 0xffff;

    private int activityHint = 0xffff;

    private int bodyLength = 0;

    private int fragmentNumber = 0;

    private int authenticationProtocol = AUTHENTICATION_PROTOCOL_NONE;

    public int getMajorVersion() {
        return CONNECTIONLESS_MAJOR_VERSION;
    }

    public Format getFormat() {
        return (format != null) ? format : (format = Format.DEFAULT_FORMAT);
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public int getFlags1() {
        return flags1;
    }

    public void setFlags1(int flags1) {
        this.flags1 = flags1;
    }

    public boolean getFlag1(int flag1) {
        return (getFlags1() & flag1) != 0;
    }

    public void setFlag1(int flag1, boolean value) {
        setFlags1(value ? (getFlags1() | flag1) :
                (getFlags1() & ~flag1));
    }

    public int getFlags2() {
        return flags2;
    }

    public void setFlags2(int flags2) {
        this.flags2 = flags2;
    }

    public boolean getFlag2(int flag2) {
        return (getFlags2() & flag2) != 0;
    }

    public void setFlag2(int flag2, boolean value) {
        setFlags2(value ? (getFlags2() | flag2) :
                (getFlags2() & ~flag2));
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public UUID getObject() {
        return object;
    }

    public void setObject(UUID object) {
        this.object = object;
    }

    public PresentationSyntax getSyntax() {
        return syntax;
    }

    public void setSyntax(PresentationSyntax syntax) {
        this.syntax = syntax;
    }

    public UUID getActivity() {
        return activity;
    }

    public void setActivity(UUID activity) {
        this.activity = activity;
    }

    public int getServerBootTime() {
        return serverBootTime;
    }

    public void setServerBootTime(int serverBootTime) {
        this.serverBootTime = serverBootTime;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getOpnum() {
        return opnum;
    }

    public void setOpnum(int opnum) {
        this.opnum = opnum;
    }

    public int getInterfaceHint() {
        return interfaceHint;
    }

    public void setInterfaceHint(int interfaceHint) {
        this.interfaceHint = interfaceHint;
    }

    public int getActivityHint() {
        return activityHint;
    }

    public void setActivityHint(int activityHint) {
        this.activityHint = activityHint;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    protected void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public int getFragmentNumber() {
        return fragmentNumber;
    }

    public void setFragmentNumber(int fragmentNumber) {
        this.fragmentNumber = fragmentNumber;
    }

    public int getAuthenticationProtocol() {
        return authenticationProtocol;
    }

    public void setAuthenticationProtocol(int authenticationProtocol) {
        this.authenticationProtocol = authenticationProtocol;
    }

    public void read(NetworkDataRepresentation ndr) {
        readPdu(ndr);
    }

    public void write(NetworkDataRepresentation ndr) {
        ndr.setFormat(getFormat());
        writePdu(ndr);
        NdrBuffer buffer = ndr.getBuffer();
        // write the body length, now that it is known.
        buffer.setIndex(BODY_LENGTH_OFFSET);
        ndr.writeUnsignedShort(getBodyLength());
        buffer.setIndex(buffer.getLength());
    }

    protected void readPdu(NetworkDataRepresentation ndr) {
        readHeader(ndr);
        if (getBodyLength() != 0) readBody(ndr);
    }

    protected void writePdu(NetworkDataRepresentation ndr) {
        writeHeader(ndr);
        NdrBuffer buffer = ndr.getBuffer();
        int start = buffer.getIndex();
        writeBody(ndr);
        setBodyLength(buffer.getIndex() - start);
    }

    protected void readHeader(NetworkDataRepresentation ndr) {
        if (ndr.readUnsignedSmall() != CONNECTIONLESS_MAJOR_VERSION) {
            throw new IllegalStateException("Version mismatch.");
        }
        if (getType() != ndr.readUnsignedSmall()) {
            throw new IllegalArgumentException("Incorrect PDU type.");
        }
        setFlags1(ndr.readUnsignedSmall());
        setFlags2(ndr.readUnsignedSmall());
        Format format = ndr.readFormat(true);
        setFormat(format);
        ndr.setFormat(format);
        int serialHigh = ndr.readUnsignedSmall();

	    UUID interfaceId = new UUID();
		try {
			UUID uuid = new UUID();
			uuid.decode(ndr, ndr.getBuffer());
	        setObject(uuid);
	
			interfaceId.decode(ndr, ndr.getBuffer());
	
			UUID act = new UUID();
			act.decode(ndr, ndr.getBuffer());
	        setActivity(act);
		} catch (NdrException ne) { }

        setServerBootTime(ndr.readUnsignedLong());
        PresentationSyntax syntax = new PresentationSyntax();
        syntax.setUuid(interfaceId);
        syntax.setVersion(ndr.readUnsignedLong());
        setSyntax(syntax);
        setSequenceNumber(ndr.readUnsignedLong());
        setOpnum(ndr.readUnsignedShort());
        setInterfaceHint(ndr.readUnsignedShort());
        setActivityHint(ndr.readUnsignedShort());
        setBodyLength(ndr.readUnsignedShort());
        setFragmentNumber(ndr.readUnsignedShort());
        setAuthenticationProtocol(ndr.readUnsignedSmall());
        setSerialNumber((serialHigh << 8) | ndr.readUnsignedSmall());
    }

    protected void writeHeader(NetworkDataRepresentation ndr) {
        ndr.writeUnsignedSmall(getMajorVersion());
        ndr.writeUnsignedSmall(getType());
        ndr.writeUnsignedSmall(getFlags1());
        ndr.writeUnsignedSmall(getFlags2());
        ndr.writeFormat(true);
        int serialNumber = getSerialNumber();
        ndr.writeUnsignedSmall((serialNumber >> 8) & 0xff);

		try {
	        UUID uuid = getObject();
			UUID nilu = new UUID(UUID.NIL_UUID);
			if (uuid == null) {
				nilu.encode(ndr, ndr.getBuffer());
			} else {
				uuid.encode(ndr, ndr.getBuffer());
			}
	
	        PresentationSyntax syntax = getSyntax();
	        uuid = syntax.getUuid();
			if (uuid == null) {
				nilu.encode(ndr, ndr.getBuffer());
			} else {
				uuid.encode(ndr, ndr.getBuffer());
			}
	
	        uuid = getActivity();
			if (uuid == null) {
				nilu.encode(ndr, ndr.getBuffer());
			} else {
				uuid.encode(ndr, ndr.getBuffer());
			}
		} catch (NdrException ne) { }

        ndr.writeUnsignedLong(getServerBootTime());
        ndr.writeUnsignedLong(syntax.getVersion());
        ndr.writeUnsignedLong(getSequenceNumber());
        ndr.writeUnsignedShort(getOpnum());
        ndr.writeUnsignedShort(getInterfaceHint());
        ndr.writeUnsignedShort(getActivityHint());
        // skip the body length, since we don't know it yet
        ndr.writeUnsignedShort(0);
        ndr.writeUnsignedShort(getFragmentNumber());
        ndr.writeUnsignedShort(getAuthenticationProtocol());
        ndr.writeUnsignedSmall((serialNumber & 0xff));
    }

    protected void readBody(NetworkDataRepresentation ndr) { }

    protected void writeBody(NetworkDataRepresentation ndr) { }

    public abstract int getType();
}
