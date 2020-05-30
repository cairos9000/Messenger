from Cryptodome.Cipher import AES
from Cryptodome.Random import random
from IDEA import IDEA


def byte_xor(ba1, ba2):
    return bytes([_a ^ _b for _a, _b in zip(ba1, ba2)])


def prep_data(u, str):
    d = str.encode("utf-8")
    res = list()
    for i in range(0, len(d), u):
        res.append(d[i:i + u])
    return res


def back_prep_data(data):
    res = b''
    for d in data:
        res += d.to_bytes(1,'big')
    return res.decode("utf-8")


data1 = "Hello world! it is pretty message! I has written it for sure!  "
data2 = "Goodbye everyone! you cannot prove that it is not a randomness!"

iv = b'Key byte sixteen'
key1 = b'Sixteen byte key'
key2 = b'Second evil key!'


# cipher1 = IDEA(byte_xor(key1, iv))
# cipher2 = IDEA(byte_xor(key2, iv))


def enc(key1, key2, iv, data1, data2):
    cipher1 = AES.new(byte_xor(key1, iv), AES.MODE_ECB)
    cipher2 = AES.new(byte_xor(key2, iv), AES.MODE_ECB)

    n = 128
    u = n // 16
    k = n - u

    d1 = prep_data(u // 8, data1)
    d2 = prep_data(u // 8, data2)
    print(d1)
    print(d2)
    c = list(d1)

    i = 0
    j = 0
    p = random.getrandbits(k)
    r = p
    while 1:
        c[i] = (cipher1.encrypt(d1[i] + r.to_bytes(k // 8, "big")))
        T = cipher2.decrypt(c[i])
        t = T[0:1]
        rr = T[1:]
        if t != d2[i]:
            if j < 2 ** (2 * u):
                j += 1
                r += 1
                continue
            else:
                print("Encryption of the pair of input data blocks ti and mi has not been fulfilled!")
        if i < len(d1) - 1:
            p = random.getrandbits(k)
            r = p
            i += 1
            j = 0
            print(i)
        else:
            break

    print(c)
    return c


def dec(key, iv, data):
    cipher = AES.new(byte_xor(key, iv), AES.MODE_ECB)
    w = list(data)
    for i in range(0, len(data)):
        w[i] = cipher.decrypt(data[i])[0]
    print(w)
    return w


c = enc(key1, key2, iv, data1, data2)
w1 = back_prep_data(dec(key1, iv, c))
w2 = back_prep_data(dec(key2, iv, c))
print(w1)
print(w2)
print(w1==data1)
print(w2==data2)
