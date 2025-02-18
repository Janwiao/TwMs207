package client.anticheat;

import client.MapleCharacter;
import client.skill.SkillFactory;
import constants.GameConstants;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import server.Timer.CheatTimer;
import tools.StringUtil;

public class CheatTracker {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = lock.readLock(), wL = lock.writeLock();
    private final Map<CheatingOffense, CheatingOffenseEntry> offenses = new LinkedHashMap<>();
    private WeakReference<MapleCharacter> chr;
    // For keeping track of speed attack hack.
    private long lastAttackTime = 0;
    private int lastAttackTickCount = 0;
    private byte Attack_tickResetCount = 0;
    private long Server_ClientAtkTickDiff = 0;
    private long lastDamage = 0;
    private long takingDamageSince;
    private int numSequentialDamage = 0;
    private long lastDamageTakenTime = 0;
    private byte numZeroDamageTaken = 0;
    private int numSequentialSummonAttack = 0;
    private long summonSummonTime = 0;
    private int numSameDamage = 0;
    private Point lastMonsterMove;
    private int monsterMoveCount;
    private int attacksWithoutHit = 0;
    private byte dropsPerSecond = 0;
    private long lastDropTime = 0;
    private byte msgsPerSecond = 0;
    private long lastMsgTime = 0;
    private ScheduledFuture<?> invalidationTask;
    private int gm_message = 0;
    private int lastTickCount = 0, tickSame = 0;
    private long lastSmegaTime = 0, lastBBSTime = 0, lastASmegaTime = 0;
    private long lastSaveTime = 0;

    // private int lastFamiliarTickCount = 0;
    // private byte Familiar_tickResetCount = 0;
    // private long Server_ClientFamiliarTickDiff = 0;
    private int numSequentialFamiliarAttack = 0;
    private long familiarSummonTime = 0;

    public CheatTracker(final MapleCharacter chr) {
        start(chr);
    }

    public final void checkAttack(final int skillId, final int tickcount) {
        final int AtkDelay = GameConstants.getAttackDelay(skillId,
                skillId == 0 ? null : SkillFactory.getSkill(skillId));
        if ((tickcount - lastAttackTickCount) < AtkDelay) {
            registerOffense(CheatingOffense.FASTATTACK);
        }
        lastAttackTime = System.currentTimeMillis();
        if (chr.get() != null && lastAttackTime - chr.get().getChangeTime() > 600000) { // chr was afk for 10 mins and
            // is now attacking
            chr.get().setChangeTime();
        }
        final long STime_TC = lastAttackTime - tickcount; // hack = - more
        if (Server_ClientAtkTickDiff - STime_TC > 1000) {
            registerOffense(CheatingOffense.FASTATTACK2);
        }
        // if speed hack, client tickcount values will be running at a faster pace
        // For lagging, it isn't an issue since TIME is running simotaniously, client
        // will be sending values of older time

        // System.out.println("Delay [" + skillId + "] = " + (tickcount -
        // lastAttackTickCount) + ", " + (Server_ClientAtkTickDiff - STime_TC));
        Attack_tickResetCount++; // Without this, the difference will always be at 100
        if (Attack_tickResetCount >= (AtkDelay <= 200 ? 1 : 4)) {
            Attack_tickResetCount = 0;
            Server_ClientAtkTickDiff = STime_TC;
        }
        updateTick(tickcount);
        lastAttackTickCount = tickcount;
    }

    // unfortunately PVP does not give a tick count
    public final void checkPVPAttack(final int skillId) {
        final int AtkDelay = GameConstants.getAttackDelay(skillId,
                skillId == 0 ? null : SkillFactory.getSkill(skillId));
        final long STime_TC = System.currentTimeMillis() - lastAttackTime; // hack = - more
        if (STime_TC < AtkDelay) {
            registerOffense(CheatingOffense.FASTATTACK);
        }
        lastAttackTime = System.currentTimeMillis();
    }

    public final long getLastAttack() {
        return lastAttackTime;
    }

    public final void checkTakeDamage(final int damage) {
        numSequentialDamage++;
        lastDamageTakenTime = System.currentTimeMillis();

        // System.out.println("tb" + timeBetweenDamage);
        // System.out.println("ns" + numSequentialDamage);
        // System.out.println(timeBetweenDamage / 1500 + "(" + timeBetweenDamage /
        // numSequentialDamage + ")");
        if (lastDamageTakenTime - takingDamageSince / 500 < numSequentialDamage) {
            registerOffense(CheatingOffense.FAST_TAKE_DAMAGE);
        }
        if (lastDamageTakenTime - takingDamageSince > 4500) {
            takingDamageSince = lastDamageTakenTime;
            numSequentialDamage = 0;
        }
        /*
         * (non-thieves) Min Miss Rate: 2% Max Miss Rate: 80% (thieves) Min Miss Rate:
         * 5% Max Miss Rate: 95%
         */
        if (damage == 0) {
            numZeroDamageTaken++;
            if (numZeroDamageTaken >= 35) { // Num count MSEA a/b players
                numZeroDamageTaken = 0;
                registerOffense(CheatingOffense.HIGH_AVOID);
            }
        } else if (damage != -1) {
            numZeroDamageTaken = 0;
        }
    }

    public final void checkSameDamage(final long dmg, final double expected) {
        if (dmg == 999999) {
            return;
        }
        if (dmg > 2000 && lastDamage == dmg && chr.get() != null
                && (chr.get().getLevel() < 175 || dmg > expected * 2)) {
            numSameDamage++;

            if (numSameDamage > 5) {
                registerOffense(CheatingOffense.SAME_DAMAGE, numSameDamage + " times, damage " + dmg + ", expected "
                        + expected + " [Level: " + chr.get().getLevel() + ", Job: " + chr.get().getJob() + "]");
                numSameDamage = 0;
            }
        } else {
            lastDamage = dmg;
            numSameDamage = 0;
        }
    }

    public final void checkMoveMonster(final Point pos) {
        if (pos == lastMonsterMove) {
            monsterMoveCount++;
            if (monsterMoveCount > 10) {
                registerOffense(CheatingOffense.MOVE_MONSTERS, "Position: " + pos.x + ", " + pos.y);
                monsterMoveCount = 0;
            }
        } else {
            lastMonsterMove = pos;
            monsterMoveCount = 1;
        }
    }

    public final void resetSummonAttack() {
        summonSummonTime = System.currentTimeMillis();
        numSequentialSummonAttack = 0;
    }

    public final boolean checkSummonAttack() {
        numSequentialSummonAttack++;
        return (System.currentTimeMillis() - summonSummonTime) / (1000 + 1) >= numSequentialSummonAttack;
    }

    public final void resetFamiliarAttack() {
        familiarSummonTime = System.currentTimeMillis();
        numSequentialFamiliarAttack = 0;
        // lastFamiliarTickCount = 0;
        // Familiar_tickResetCount = 0;
        // Server_ClientFamiliarTickDiff = 0;
    }

    public final boolean checkFamiliarAttack(final MapleCharacter chr) {
        /*
         * final int tickdifference = (tickcount - lastFamiliarTickCount); if
         * (tickdifference < 500) {
         * chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK); }
         * final long STime_TC = System.currentTimeMillis() - tickcount; final long
         * S_C_Difference = Server_ClientFamiliarTickDiff - STime_TC; if (S_C_Difference
         * > 500) {
         * chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK); }
         * Familiar_tickResetCount++; if (Familiar_tickResetCount > 4) {
         * Familiar_tickResetCount = 0; Server_ClientFamiliarTickDiff = STime_TC; }
         * lastFamiliarTickCount = tickcount;
         */
        numSequentialFamiliarAttack++;
        // estimated
        // System.out.println(numMPRegens + "/" + allowedRegens);
        if ((System.currentTimeMillis() - familiarSummonTime) / (600 + 1) < numSequentialFamiliarAttack) {
            registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
            return false;
        }
        return true;
    }

    public final void checkDrop() {
        checkDrop(false);
    }

    public final void checkDrop(final boolean dc) {
        if ((System.currentTimeMillis() - lastDropTime) < 1000) {
            dropsPerSecond++;
            if (dropsPerSecond >= (dc ? 32 : 16) && chr.get() != null) {
                if (dc) {
                    chr.get().getClient().getSession().close();
                    System.err.println("伺服器主動斷開用戶端連結,調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
                } else {
                    chr.get().getClient().setMonitored(true);
                }
            }
        } else {
            dropsPerSecond = 0;
        }
        lastDropTime = System.currentTimeMillis();
    }

    public final void checkMsg() { // ALL types of msg. caution with number of msgsPerSecond
        if ((System.currentTimeMillis() - lastMsgTime) < 1000) { // luckily maplestory has auto-check for too much
            // msging
            msgsPerSecond++;
            if (msgsPerSecond > 10 && chr.get() != null) {
                chr.get().getClient().getSession().close();
                System.err.println("伺服器主動斷開用戶端連結,調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
        } else {
            msgsPerSecond = 0;
        }
        lastMsgTime = System.currentTimeMillis();
    }

    public final int getAttacksWithoutHit() {
        return attacksWithoutHit;
    }

    public final void setAttacksWithoutHit(final boolean increase) {
        if (increase) {
            this.attacksWithoutHit++;
        } else {
            this.attacksWithoutHit = 0;
        }
    }

    public final void registerOffense(final CheatingOffense offense) {
        registerOffense(offense, null);
    }

    public final void registerOffense(final CheatingOffense offense, final String param) {
        final MapleCharacter chrhardref = chr.get();
        if (chrhardref == null || !offense.isEnabled() || chrhardref.isClone()) {
            return;
        }
        // System.out.println("OFFENSE REGISTERED: " + offense.name() + " on " +
        // chrhardref.getName());
        CheatingOffenseEntry entry = null;
        rL.lock();
        try {
            entry = offenses.get(offense);
        } finally {
            rL.unlock();
        }
        if (entry != null && entry.isExpired()) {
            expireEntry(entry);
            entry = null;
            gm_message = 0;
        }
        if (entry == null) {
            entry = new CheatingOffenseEntry(offense, chrhardref.getId());
        }
        if (param != null) {
            entry.setParam(param);
        }
        entry.incrementCount();
        // if (offense.shouldAutoban(entry.getCount())) {
        // final byte type = offense.getBanType();
        // if (type == 1) {
        // // AutobanManager.getInstance().autoban(chrhardref.getClient(),
        // StringUtil.makeEnumHumanReadable(offense.name()));
        // } else if (type == 2) {
        // chrhardref.getClient().getSession().close();
        // System.err.println("伺服器主動斷開用戶端連結,調用位置: " + new
        // java.lang.Throwable().getStackTrace()[0]);
        // }
        // gm_message = 0;
        // return;
        // }
        wL.lock();
        try {
            offenses.put(offense, entry);
        } finally {
            wL.unlock();
        }
        switch (offense) {
            // case HIGH_DAMAGE_MAGIC:
            case HIGH_DAMAGE_MAGIC_2:
            // case HIGH_DAMAGE:
            case HIGH_DAMAGE_2:
            case ATTACK_FARAWAY_MONSTER:
            case ATTACK_FARAWAY_MONSTER_SUMMON:
            case SAME_DAMAGE:
                gm_message++;
                if (gm_message % 100 == 0) {
                    // World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM Message]
                    // " + MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + " (level " +
                    // chrhardref.getLevel() + ") suspected of hacking! " +
                    // StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : (" -
                    // " + param))));
                }
                if (gm_message >= 300 && chrhardref.getLevel() < (offense == CheatingOffense.SAME_DAMAGE ? 175 : 150)) {
                    final Timestamp created = chrhardref.getClient().getCreated();
                    long time = System.currentTimeMillis();
                    if (created != null) {
                        time = created.getTime();
                    }
                    if (time + (15 * 24 * 60 * 60 * 1000) >= System.currentTimeMillis()) { // made within 15
                        // AutobanManager.getInstance().autoban(chrhardref.getClient(),
                        // StringUtil.makeEnumHumanReadable(offense.name()) + " over 500 times " +
                        // (param == null ? "" : (" - " + param)));
                    } else {
                        gm_message = 0;
                        // World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6, "[GM Message]
                        // " + MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + " (level " +
                        // chrhardref.getLevel() + ") suspected of autoban! " +
                        // StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : (" -
                        // " + param))));
                        // FileoutputUtil.log(FileoutputUtil.Hacker_Log, "[GM Message] " +
                        // MapleCharacterUtil.makeMapleReadable(chrhardref.getName()) + " (level " +
                        // chrhardref.getLevel() + ") suspected of autoban! " +
                        // StringUtil.makeEnumHumanReadable(offense.name()) + (param == null ? "" : (" -
                        // " + param)));
                    }
                }
                break;
        }
        CheatingOffensePersister.getInstance().persistEntry(entry);
    }

    public void updateTick(int newTick) {
        if (newTick <= lastTickCount) { // definitely packet spamming or the added feature in many PEs which is to
            // generate random tick
            if (tickSame >= 15 && chr.get() != null) {
                chr.get().getClient().getSession().close();
                System.err.println("伺服器主動斷開用戶端連結,調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            } else {
                tickSame++;
            }
        } else {
            tickSame = 0;
        }
        lastTickCount = newTick;
    }

    public boolean canSmega() {
        if (lastSmegaTime + 15000 > System.currentTimeMillis() && chr.get() != null && !chr.get().isGM()) {
            return false;
        }
        lastSmegaTime = System.currentTimeMillis();
        return true;
    }

    public boolean canAvatarSmega() {
        if (lastASmegaTime + 300000 > System.currentTimeMillis() && chr.get() != null && !chr.get().isGM()) {
            return false;
        }
        lastASmegaTime = System.currentTimeMillis();
        return true;
    }

    public boolean canBBS() {
        if (lastBBSTime + 60000 > System.currentTimeMillis() && chr.get() != null) {
            return false;
        }
        lastBBSTime = System.currentTimeMillis();
        return true;
    }

    public final void expireEntry(final CheatingOffenseEntry coe) {
        wL.lock();
        try {
            offenses.remove(coe.getOffense());
        } finally {
            wL.unlock();
        }
    }

    public final int getPoints() {
        int ret = 0;
        CheatingOffenseEntry[] offenses_copy;
        rL.lock();
        try {
            offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
        } finally {
            rL.unlock();
        }
        for (final CheatingOffenseEntry entry : offenses_copy) {
            if (entry.isExpired()) {
                expireEntry(entry);
            } else {
                ret += entry.getPoints();
            }
        }
        return ret;
    }

    public final Map<CheatingOffense, CheatingOffenseEntry> getOffenses() {
        return Collections.unmodifiableMap(offenses);
    }

    public final String getSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<CheatingOffenseEntry> offenseList = new ArrayList<>();
        rL.lock();
        try {
            for (final CheatingOffenseEntry entry : offenses.values()) {
                if (!entry.isExpired()) {
                    offenseList.add(entry);
                }
            }
        } finally {
            rL.unlock();
        }
        Collections.sort(offenseList, new Comparator<CheatingOffenseEntry>() {

            @Override
            public final int compare(final CheatingOffenseEntry o1, final CheatingOffenseEntry o2) {
                final int thisVal = o1.getPoints();
                final int anotherVal = o2.getPoints();
                return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
            }
        });
        final int to = Math.min(offenseList.size(), 4);
        for (int x = 0; x < to; x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).getOffense().name()));
            ret.append(": ");
            ret.append(offenseList.get(x).getCount());
            if (x != to - 1) {
                ret.append(" ");
            }
        }
        return ret.toString();
    }

    public final void dispose() {
        if (invalidationTask != null) {
            invalidationTask.cancel(false);
        }
        invalidationTask = null;
        chr = new WeakReference<>(null);
    }

    public final void start(final MapleCharacter chr) {
        this.chr = new WeakReference<>(chr);
        invalidationTask = CheatTimer.getInstance().register(new InvalidationTask(), 60000);
        takingDamageSince = System.currentTimeMillis();
    }

    public boolean canSaveDB() {
        if ((System.currentTimeMillis() - lastSaveTime < 5 * 60 * 1000)) {
            return false;
        }
        this.lastSaveTime = System.currentTimeMillis();
        return true;
    }

    public int getlastSaveTime() {
        return (int) ((System.currentTimeMillis() - this.lastSaveTime) / 1000);
    }

    private final class InvalidationTask implements Runnable {

        @Override
        public final void run() {
            CheatingOffenseEntry[] offenses_copy;
            rL.lock();
            try {
                offenses_copy = offenses.values().toArray(new CheatingOffenseEntry[offenses.size()]);
            } finally {
                rL.unlock();
            }
            for (CheatingOffenseEntry offense : offenses_copy) {
                if (offense.isExpired()) {
                    expireEntry(offense);
                }
            }
            if (chr.get() == null) {
                dispose();
            }
        }
    }
}
