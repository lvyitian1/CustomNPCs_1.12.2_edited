package noppes.npcs.ability;

import noppes.npcs.api.event.NpcEvent$DamagedEvent;

public interface IAbilityDamaged extends IAbility {
   void handleEvent(NpcEvent$DamagedEvent var1);
}
