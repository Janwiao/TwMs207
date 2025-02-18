/*
 * This file is part of the OdinMS MapleStory Private Server
 * Copyright (C) 2011 Patrick Huy and Matthias Butz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server;

/**
 *
 * @author AlphaEta
 */
public enum MapleStatInfo {

    PVPdamage(0), // Battle Mode ATT Increase
    abnormalDamR(0), // Additional Damage on Targets with Abnormal Status
    acc(0), // Increase Accuracy +
    acc2dam(0), // Weapon Accuracy or Magic Accuracy (higher) to Damage
    acc2mp(0), // Accuracy to Max MP
    accR(0), // Accuracy %
    accX(0), // Accuracy +
    ar(0), // Accuracy %
    asrR(0), // Abnormal Status Resistance %
    attackCount(1), // Number of attacks, similiar to bulletCount
    bdR(0), // Damage on Bosses %
    bufftimeR(0), // Buff Skill duration increase %
    bulletConsume(0), // Consume bullets
    bulletCount(1), // Number of attacks hit
    coolTimeR(0), // Reduce Skill cooldown %
    cooltime(0), // Cooldown time
    cr(0), // Critical %
    criticaldamageMax(0), // Critical Maximum Damage Increase +
    criticaldamageMin(0), // Minimum Critical Damage Increase +
    damR(0), // Damage %
    damPlus(0), damage(100), // Damage %
    damagepc(0), // Rage of Pharaoh and Bamboo Rain has this, drop from sky?
    dateExpire(0), // Useless date stuffs
    dex(0), // Increase DEX +
    dex2str(0), // DEX to STR
    dexFX(0), // Increase DEX
    dexX(0), // Increase DEX
    dexR(0), dot(0), // Damage over time %
    dotInterval(0), // Damage dealt at intervals
    dotSuperpos(1), // Damage over time stack
    dotTime(0), // DOT time length (Lasts how long)
    dropR(0), // Increases Drop %
    emad(0), // Enhanced Magic ATT
    emdd(0), // Enhanced Magic DEF
    emhp(0), // Enhanced HP
    emmp(0), // Enhanced MP
    epad(0), // Enhanced ATT
    epdd(0), // Enhanced DEF
    er(0), // Avoidability %
    eva(0), // Avoidability Increase, avoid
    eva2hp(0), // Convert Avoidability to HP
    evaR(0), // Avoidability %
    evaX(0), // Avoidability Increase
    expLossReduceR(0), // Reduce EXP loss at death %
    expR(0), // Additional % EXP
    extendPrice(0), // [Guild] Extend price
    finalAttackDamR(0), // Additional damage from Final Attack skills %
    soulmpCon(0), fixdamage(0), // Fixed damage dealt upon using skill
    forceCon(0), // Fury Cost
    gauge(0), hcCooltime(0), hcHp(0), hcProp(0), hcReflect(0), hcSubProp(0), hcSubTime(0), hcSummonHp(0), hcTime(
            0), MDF(0), powerCon(0), // Surplus Energy Cost
    ppRecovery(0), ppCon(0), hp(0), // Restore HP/Heal
    hpCon(0), // HP Consumed
    hpRCon(0), // HP% Consumed
    iceGageCon(0), // Ice Gauge Cost
    ignoreMobDamR(0), // Ignore Mob Damage to Player %
    ignoreMobpdpR(0), // Ignore Mob DEF % -> Attack higher
    indieIgnoreMobpdpR(0), indieAcc(0), // Accuracy +
    indieAllStat(0), // All Stats +
    indieDamR(0), // Damage Increase %
    indieEva(0), // Avoidability +
    indieJump(0), // Jump Increase +
    indieMad(0), // Magic Damage Increase
    indieMadR(0), indieMhp(0), // Max HP Increase +
    indiePdd(0), // ??
    indieMdd(0), // MDEF?
    indieTerR(0), indieBDR(0), indieAsrR(0), indieMhpR(0), // Max HP Increase %
    indieMmp(0), // Max MP Increase +
    indieMmpR(0), // Max MP Increase %
    indiePad(0), // Damage Increase
    indiePadR(0), indieSpeed(0), // Speed +
    indieBooster(0), // Attack Speed
    indieCr(0), // Critical?
    indieStance(0), // ?
    indieMaxDamageOver(0), // MaxDmg Inc over x
    IgnoreTargetDEF(0), indieExp(0), int2luk(0), // Convert INT to LUK
    intFX(0), // Increase INT
    intX(0), // Increase INT
    int_(0, true), intR(0), itemCon(0), // Consumes item upon using <itemid>
    itemConNo(0), // amount for above
    itemConsume(0), // Uses certain item to cast that attack, the itemid doesn't need to be in
    // inventory, just the effect.
    jump(0), // Jump Increase
    kp(0), // Body count attack stuffs
    luk2dex(0), // Convert LUK to DEX
    lukFX(0), // Increase LUK
    lukX(0), // Increase LUK
    luk(0), lukR(0), lv2damX(0), // Additional damage per character level
    lv2mad(0), // Additional magic damage per character level
    lv2mdX(0), // Additional magic defense per character level
    lv2pad(0), // Additional damage per character level
    lv2pdX(0), // Additional defense per character level
    mad(0), // 魔法攻擊力+
    madX(0), // 魔法攻擊力 +
    madR(0), // 魔法攻擊力%
    mastery(0), // Increase mastery
    mdd(0), // Magic DEF
    mdd2dam(0), // Magic DEF to Damage
    mdd2pdd(0), // Magic DEF to Weapon DEF
    mdd2pdx(0), // When hit with a physical attack, damage equal to #mdd2pdx% of Magic DEF is
    // ignored
    mddR(0), // Magic DEF %
    mddX(0), // Magic DEF
    mesoR(0), // Mesos obtained + %
    mhp2damX(0), // Max HP added as additional damage
    mhpR(0), // Max HP %
    mhpX(0), // Max HP +
    minionDeathProp(0), // Instant kill on normal monsters in Azwan Mode
    mmp2damX(0), // Max MP added as additional damage
    lv2mhp(0), lv2mmp(0), mmpR(0), // Max MP %
    mmpX(0), // Max MP +
    onActive(0), // Chance to recharge skill
    mobCount(1), // Max Enemies hit
    morph(0), // MORPH ID
    mp(0), // Restore MP/Heal
    mpCon(0), // MP Cost
    mpConEff(0), // MP Potion effect increase %
    mpConReduce(0), // MP Consumed reduce
    nbdR(0), // Increases damage by a set percentage when attacking a normal monster.
    nocoolProp(0), // When using a skill, Cooldown is not applied at #nocoolProp% chance. Has no
    // effect on skills without Cooldown.
    onHitHpRecoveryR(0), // Chance to recover HP when attacking.
    onHitMpRecoveryR(0), // Chance to recover MP when attacking.
    pad(0), // Attack +
    padX(0), // Attack +
    padR(0), passivePlus(0), // Increases level of passive skill by #
    pdd(0), // Weapon DEF
    pdd2dam(0), // Weapon DEF added as additional damage
    pdd2mdd(0), // Weapon DEF added to Magic DEF
    pdd2mdx(0), // When hit with a magical attack, damage equal to #pdd2mdx% of Weapon DEF is
    // ignored
    pddR(0), // Weapon DEF %
    pddX(0), // Weapon DEF
    pdR(0), // 傷害增加
    stanceProp(0), // 格擋(泰山)
    period(0), // [Guild/Professions] time taken
    price(0), // [Guild] price to purchase
    priceUnit(0), // [Guild] Price stuffs
    prop(100), // Percentage chance over 100%
    psdJump(0), // Passive Jump Increase
    psdSpeed(0), // Passive Speed Increase
    range(0), // Range
    reqGuildLevel(0), // [Guild] guild req level
    selfDestruction(0), // Self Destruct Damage
    speed(0), // Increase moving speed
    speedMax(0), // Max Movement Speed +
    str(0), // Increase STR
    str2dex(0), // STR to DEX
    strFX(0), // Increase STR
    strX(0), // Increase STR
    strR(0), subProp(0), // Summon Damage Prop
    subTime(-1), // Summon Damage Effect time
    suddenDeathR(0), // Instant kill on enemy %
    summonTimeR(0), // Summon Duration + %
    targetPlus(0), // Increases the number of enemies hit by multi-target skill
    tdR(0), // Increases damage by a set percentage when attacking a tower
    terR(0), // Elemental Resistance %
    time(-1), // bufflength
    s(0), t(0), // Damage taken reduce
    u(0), v(0), w(0), x(0), y(0), z(0), q(0),
    // lt(0), //box stuffs
    // lt2(0), //Tempest has this
    // rb(0), //rectangle box
    // rb2(0), //Tempest has this..
    lv2str(0), lv2dex(0), lv2int(0), lv2luk(0), killRecoveryR(0), // 擊殺敵人時，恢復HP
    damAbsorbShieldR(0), dotHealHPPerSecondR(0), dotHealMPPerSecondR(0);
    private final int def;
    private final boolean special;

    private MapleStatInfo(final int def) {
        this.def = def;
        this.special = false;
    }

    private MapleStatInfo(final int def, final boolean special) {
        this.def = def;
        this.special = special;
    }

    public final int getDefault() {
        return def;
    }

    public final boolean isSpecial() {
        return special;
    }
}
