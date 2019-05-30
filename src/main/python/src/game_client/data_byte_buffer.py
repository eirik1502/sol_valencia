import struct


INT_BYTES = 4
FLOAT_BYTES = 4
BOOL_BYTES = 1

def bytes_to_int(bytes):
    return struct.unpack('>i', bytes)[0]  # > means big endian
def bytes_to_float(bytes):
    return struct.unpack('>f', bytes)[0]
def bytes_to_bool(bytes):
    return struct.unpack('>?', bytes)[0]

def int_to_bytes(i):
    return int(i).to_bytes(INT_BYTES, byteorder='big', signed=True)
def float_to_bytes(f):
    return struct.pack('>f', float(f))
def bool_to_bytes(b):
    return struct.pack('>?', bool(b))


class DataByteBuffer:

    def __init__(self, init_bytes=b''):
        self.byte_buff = init_bytes

    def writeInt(self, data):
        self.byte_buff += int_to_bytes(data)

    def writeFloat(self, data):
        self.byte_buff += float_to_bytes(data)

    def writeBool(self, data):
        self.byte_buff += bool_to_bytes(data)


    def readInt(self):
        data_bytes, self.byte_buff = self.byte_buff[:INT_BYTES], self.byte_buff[INT_BYTES:]
        return bytes_to_int(data_bytes)

    def readFloat(self):
        data_bytes, self.byte_buff = self.byte_buff[:FLOAT_BYTES], self.byte_buff[FLOAT_BYTES:]
        return bytes_to_float(data_bytes)

    def readBool(self):
        data_bytes, self.byte_buff = self.byte_buff[:BOOL_BYTES], self.byte_buff[BOOL_BYTES:]
        return bytes_to_bool(data_bytes)

    def getBytes(self):
        return self.byte_buff
