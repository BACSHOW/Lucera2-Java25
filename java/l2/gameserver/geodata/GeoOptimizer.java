/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.geodata;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoOptimizer {
    private static final Logger log = LoggerFactory.getLogger(GeoOptimizer.class);
    public static int[][][] checkSums;
    private static final byte xM = 1;

    public static BlockLink[] loadBlockMatches(String string) {
        File file = new File(Config.DATAPACK_ROOT, string);
        if (!file.exists()) {
            return null;
        }
        try {
            FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
            int n = (int)((fileChannel.size() - 1L) / 6L);
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fileChannel.size());
            fileChannel.close();
            mappedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if (mappedByteBuffer.get() != 1) {
                return null;
            }
            BlockLink[] blockLinkArray = new BlockLink[n];
            for (int i = 0; i < blockLinkArray.length; ++i) {
                blockLinkArray[i] = new BlockLink(mappedByteBuffer.getShort(), mappedByteBuffer.get(), mappedByteBuffer.get(), mappedByteBuffer.getShort());
            }
            return blockLinkArray;
        }
        catch (Exception exception) {
            log.error("", exception);
            return null;
        }
    }

    public static class BlockLink {
        public final int blockIndex;
        public final int linkBlockIndex;
        public final byte linkMapX;
        public final byte linkMapY;

        public BlockLink(short s, byte by, byte by2, short s2) {
            this.blockIndex = s & 0xFFFF;
            this.linkMapX = by;
            this.linkMapY = by2;
            this.linkBlockIndex = s2 & 0xFFFF;
        }

        public BlockLink(int n, byte by, byte by2, int n2) {
            this.blockIndex = n & 0xFFFF;
            this.linkMapX = by;
            this.linkMapY = by2;
            this.linkBlockIndex = n2 & 0xFFFF;
        }
    }

    public static class CheckSumLoader
    extends RunnableImpl {
        private final int xN;
        private final int xO;
        private final int xP;
        private final int xQ;
        private final byte[][][] xR;
        private final String fileName;

        public CheckSumLoader(int n, int n2, byte[][][] byArray) {
            this.xN = n;
            this.xO = n2;
            this.xP = this.xN + Config.GEO_X_FIRST;
            this.xQ = n2 + Config.GEO_Y_FIRST;
            this.xR = byArray;
            this.fileName = "geodata/checksum/" + this.xP + "_" + this.xQ + ".crc";
        }

        private boolean br() {
            File file = new File(Config.DATAPACK_ROOT, this.fileName);
            if (!file.exists()) {
                return false;
            }
            try {
                FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
                if (fileChannel.size() != 262144L) {
                    fileChannel.close();
                    return false;
                }
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fileChannel.size());
                fileChannel.close();
                mappedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int[] nArray = new int[65536];
                for (int i = 0; i < 65536; ++i) {
                    nArray[i] = mappedByteBuffer.getInt();
                }
                GeoOptimizer.checkSums[this.xN][this.xO] = nArray;
                return true;
            }
            catch (Exception exception) {
                log.error("", exception);
                return false;
            }
        }

        private void bs() {
            log.info("Saving checksums to: " + this.fileName);
            try {
                File file = new File(Config.DATAPACK_ROOT, this.fileName);
                if (file.exists()) {
                    file.delete();
                }
                FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0L, 262144L);
                mappedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int[] nArray = checkSums[this.xN][this.xO];
                for (int i = 0; i < 65536; ++i) {
                    mappedByteBuffer.putInt(nArray[i]);
                }
                fileChannel.close();
            }
            catch (Exception exception) {
                log.error("", exception);
            }
        }

        private void bt() {
            log.info("Generating checksums for " + this.xP + "_" + this.xQ);
            int[] nArray = new int[65536];
            CRC32 cRC32 = new CRC32();
            for (int i = 0; i < 65536; ++i) {
                cRC32.update(this.xR[i][0]);
                nArray[i] = (int)(cRC32.getValue() ^ 0xFFFFFFFFFFFFFFFFL);
                cRC32.reset();
            }
            GeoOptimizer.checkSums[this.xN][this.xO] = nArray;
        }

        @Override
        public void runImpl() throws Exception {
            if (!this.br()) {
                this.bt();
                this.bs();
            }
        }
    }

    public static class GeoBlocksMatchFinder
    extends RunnableImpl {
        private final int xN;
        private final int xO;
        private final int xP;
        private final int xQ;
        private final int xS;
        private final String fileName;

        public GeoBlocksMatchFinder(int n, int n2, int n3) {
            this.xN = n;
            this.xO = n2;
            this.xP = this.xN + Config.GEO_X_FIRST;
            this.xQ = this.xO + Config.GEO_Y_FIRST;
            this.xS = n3;
            this.fileName = "geodata/matches/" + this.xP + "_" + this.xQ + ".matches";
        }

        private boolean exists() {
            return new File(Config.DATAPACK_ROOT, this.fileName).exists();
        }

        private void a(BlockLink[] blockLinkArray) {
            log.info("Saving matches to: " + this.fileName);
            try {
                File file = new File(Config.DATAPACK_ROOT, this.fileName);
                if (file.exists()) {
                    file.delete();
                }
                FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0L, blockLinkArray.length * 6 + 1);
                mappedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                mappedByteBuffer.put((byte)1);
                for (int i = 0; i < blockLinkArray.length; ++i) {
                    mappedByteBuffer.putShort((short)blockLinkArray[i].blockIndex);
                    mappedByteBuffer.put(blockLinkArray[i].linkMapX);
                    mappedByteBuffer.put(blockLinkArray[i].linkMapY);
                    mappedByteBuffer.putShort((short)blockLinkArray[i].linkBlockIndex);
                }
                fileChannel.close();
            }
            catch (Exception exception) {
                log.error("", exception);
            }
        }

        private void a(int[] nArray, int n, int n2, List<BlockLink> list, boolean[] blArray) {
            int[] nArray2 = checkSums[n][n2];
            if (nArray2 == null) {
                return;
            }
            block0: for (int i = 0; i < 65536; ++i) {
                int n3;
                if (!blArray[i]) continue;
                for (int j = n3 = nArray2 == nArray ? i + 1 : 0; j < 65536; ++j) {
                    if (nArray[i] != nArray2[j] || !GeoEngine.compareGeoBlocks(this.xN, this.xO, i, n, n2, j)) continue;
                    list.add(new BlockLink(i, (byte)n, (byte)n2, j));
                    blArray[i] = false;
                    continue block0;
                }
            }
        }

        private BlockLink[] bu() {
            log.info("Searching matches for " + this.xP + "_" + this.xQ);
            long l = System.currentTimeMillis();
            boolean[] blArray = new boolean[65536];
            for (int i = 0; i < 65536; ++i) {
                blArray[i] = true;
            }
            ArrayList<BlockLink> arrayList = new ArrayList<BlockLink>();
            int[] nArray = checkSums[this.xN][this.xO];
            int n = 0;
            for (int i = this.xN; i < World.WORLD_SIZE_X; ++i) {
                int n2;
                for (int j = n2 = i == this.xN ? this.xO : 0; j < World.WORLD_SIZE_Y; ++j) {
                    this.a(nArray, i, j, arrayList, blArray);
                    if (this.xS <= 0 || this.xS != ++n) continue;
                    return arrayList.toArray(new BlockLink[arrayList.size()]);
                }
            }
            l = System.currentTimeMillis() - l;
            log.info("Founded " + arrayList.size() + " matches for " + this.xP + "_" + this.xQ + " in " + (float)l / 1000.0f + "s");
            return arrayList.toArray(new BlockLink[arrayList.size()]);
        }

        @Override
        public void runImpl() throws Exception {
            if (!this.exists()) {
                BlockLink[] blockLinkArray = this.bu();
                this.a(blockLinkArray);
            }
        }
    }
}

