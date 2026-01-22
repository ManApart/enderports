import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import org.manapart.enderports.DATA_NAME
import org.manapart.enderports.TeleporterNetwork

//package org.manapart.enderports
//
//import com.mojang.serialization.Codec
//import com.mojang.serialization.DataResult
//import com.mojang.serialization.JsonOps
//import com.mojang.serialization.codecs.RecordCodecBuilder
//import net.minecraft.core.BlockPos
//import net.minecraft.world.level.saveddata.SavedData
//import net.minecraft.world.level.saveddata.SavedDataType
//import java.util.function.Supplier
//
//
//class TeleporterNetworkData(val network: MutableMap<String, MutableSet<BlockPos>> = mutableMapOf()) : SavedData() {
//    companion object {
//        private val POS_SET_CODEC: Codec<MutableSet<BlockPos>> = BlockPos.CODEC.listOf().xmap(
//            { list -> list.toMutableSet() },
//            { set -> set.toList() }
//        )
//
//        val CODEC: Codec<TeleporterNetworkData> = RecordCodecBuilder.create { instance ->
//            instance.group(
//                Codec.unboundedMap(Codec.STRING, POS_SET_CODEC).fieldOf("network").forGetter { it.network }
//            ).apply(instance) { networkMap -> TeleporterNetworkData(networkMap.toMutableMap()) }
//        }
//
//        val TYPE = SavedDataType<TeleporterNetworkData>(
//            DATA_NAME,
//            { TeleporterNetworkData() },
//            CODEC,
//            null
//        )
//    }
//}
////
////val TP_NETWORK_CODEC: Codec<TeleporterNetworkData> = RecordCodecBuilder.create { instance ->
////instance.group()
////}
//
//val CODEC3: Codec<TeleporterNetworkData> = RecordCodecBuilder.create<TeleporterNetworkData> { instance ->
//    instance!!.group<MutableMap<String, MutableSet<BlockPos>>>(
//        Codec.unboundedMap<MutableMap<String, MutableSet<BlockPos>>>().fieldOf("data").forGetter { it.data }
//    ).apply<TeleporterNetworkData>(instance) { TeleporterNetworkData() }
//}
//
//val CODEC2: Codec<TeleporterNetworkData> = Codec.INT.xmap<SavedBlockData?>(
//    { TeleporterNetworkData() },
//    SavedBlockData::getBlocksBroken // Return the number from the 'SavedBlockData' to be saved/
//)
//
//var TP_NETWORK_CODEC: Codec<MutableMap<String, List<BlockPos>>> = Codec.unboundedMap<String, List<BlockPos>>(Codec.STRING, BlockPos.CODEC.listOf())
//
//val TYPE: SavedDataType<TeleporterNetworkData> = SavedDataType<TeleporterNetworkData>(
//    "enderports_network",
//    Supplier { TeleporterNetworkData() },
//    TP_NETWORK_CODEC,
//    null
//)
//
//// Use it to serialize data
//var result: DataResult<JsonElement>? = mapCodec.encodeStart<T?>(
//    JsonOps.INSTANCE, java.util.Map.of<K?, V?>(
//        Identifier.fromNamespaceAndPath("example", "number"), 23,
//        Identifier.fromNamespaceAndPath("example", "the_cooler_number"), 42
//    )
//)
