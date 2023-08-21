package com.jarlingwar.adminapp.utils.geo.geohash

import android.location.Location
import android.os.Parcel
import android.os.Parcelable

open class GeoHash : Parcelable {
    private var bits: Long = 0
    private var significantBits: Byte = 0
    var boundingBox: BoundingBox
        private set

    private val ord: Long
        get() = bits ushr (MAX_BIT_PRECISION - significantBits)


    constructor(parcel: Parcel) {
        bits = parcel.readLong()
        significantBits = parcel.readByte()
        boundingBox = parcel.readParcelable(BoundingBox::class.java.classLoader)!!
    }

    @JvmOverloads
    constructor(lat: Double, lon: Double, charsCount: Int = MAX_CHARACTER_PRECISION) {
        val desiredPrecision = precisionFromCharCount(charsCount)
        val precision = Math.min(desiredPrecision, MAX_BIT_PRECISION)

        var isEvenBit = true
        val latRange = doubleArrayOf(-LATITUDE_MAX_ABS, LATITUDE_MAX_ABS)
        val lonRange = doubleArrayOf(-LONGITUDE_MAX_ABS, LONGITUDE_MAX_ABS)

        while (significantBits < precision) {
            if (isEvenBit) {
                divideRangeEncode(lon, lonRange)
            } else {
                divideRangeEncode(lat, latRange)
            }
            isEvenBit = !isEvenBit
        }

        boundingBox = BoundingBox(
            generateLocation(latRange[0], lonRange[0]),
            generateLocation(latRange[1], lonRange[1])
        )
        bits = bits shl (MAX_BIT_PRECISION - precision)
    }

    constructor(hashVal: Long, significantBits: Byte) {
        var isEvenBit = true
        val latRange = doubleArrayOf(-LATITUDE_MAX_ABS, LATITUDE_MAX_ABS)
        val lonRange = doubleArrayOf(-LONGITUDE_MAX_ABS, LONGITUDE_MAX_ABS)

        var binStr = java.lang.Long.toBinaryString(hashVal)

        while (binStr.length < MAX_BIT_PRECISION) {
            binStr = "0$binStr"
        }

        for (i in 0 until significantBits) {
            if (isEvenBit) {
                divideRangeDecode(lonRange, binStr[i] != '0')
            } else {
                divideRangeDecode(latRange, binStr[i] != '0')
            }
            isEvenBit = !isEvenBit
        }

        boundingBox = BoundingBox(
            generateLocation(latRange[0], lonRange[0]),
            generateLocation(latRange[1], lonRange[1])
        )
        bits = bits shl (MAX_BIT_PRECISION - this.significantBits)
    }

    fun next(step: Int) = GeoHash((ord + step) shl MAX_BIT_PRECISION - significantBits, significantBits)

    operator fun inc() = next(1)

    operator fun dec() = next(-1)

    override fun toString(): String {
        checkConvert()
        val buf = StringBuilder()
        val firstBitFlag = -0x800000000000000L
        var bitsCopy = bits
        val partialChunks = Math.ceil((significantBits / BASE32_BITS).toDouble()).toInt()
        for (i in 0 until partialChunks) {
            buf.append(base32[(bitsCopy.and(firstBitFlag)).ushr(59).toInt()])
            bitsCopy = bitsCopy shl BASE32_BITS
        }
        return buf.toString()
    }

    private fun precisionFromCharCount(charsCount: Int): Int {
        if (charsCount > MAX_CHARACTER_PRECISION) {
            throw IllegalArgumentException("A geohash can only be $MAX_CHARACTER_PRECISION character long.")
        }
        return if (charsCount * BASE32_BITS <= MAX_GEO_HASH_BITS_COUNT)
            charsCount * BASE32_BITS
        else
            MAX_GEO_HASH_BITS_COUNT
    }

    private fun checkConvert() {
        if (significantBits % BASE32_BITS != 0) {
            throw IllegalStateException("Cannot convert a geoHash to base32")
        }
    }

    //encode

    private fun divideRangeEncode(value: Double, range: DoubleArray) {
        val mid = (range[0] + range[1]) / 2
        if (value >= mid) {
            addOnBitToEnd()
            range[0] = mid
        } else {
            addOffBitToEnd()
            range[1] = mid
        }
    }

    private fun addOnBitToEnd() {
        significantBits++
        bits = bits shl 1
        bits = bits or 0x1
    }

    private fun addOffBitToEnd() {
        significantBits++
        bits = bits shl 1
    }

    private fun generateLocation(lat: Double, lon: Double) = Location(GeoHash::javaClass.name).also {
        it.latitude = lat
        it.longitude = lon
    }

    //decode
    private fun divideRangeDecode(range: DoubleArray, b: Boolean) {
        val mid = (range[0] + range[1]) / 2
        if (b) {
            addOnBitToEnd()
            range[0] = mid
        } else {
            addOffBitToEnd()
            range[1] = mid
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeoHash

        if (bits != other.bits) return false
        if (significantBits != other.significantBits) return false
        if (boundingBox != other.boundingBox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bits.hashCode()
        result = 31 * result + significantBits
        result = 31 * result + boundingBox.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(bits)
        parcel.writeByte(significantBits)
        parcel.writeParcelable(boundingBox, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<GeoHash> {

        const val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"
        const val BASE32_BITS = 5
        const val MAX_CHARACTER_PRECISION = 12
        const val MAX_GEO_HASH_BITS_COUNT = BASE32_BITS * MAX_CHARACTER_PRECISION

        const val LATITUDE_MAX_ABS = 90.0
        const val LONGITUDE_MAX_ABS = 180.0

        val MAX_BIT_PRECISION = java.lang.Long.bitCount(Long.MAX_VALUE) + 1// max - 64;

        override fun createFromParcel(parcel: Parcel) = GeoHash(parcel)

        override fun newArray(size: Int): Array<GeoHash?> = arrayOfNulls(size)
    }
}