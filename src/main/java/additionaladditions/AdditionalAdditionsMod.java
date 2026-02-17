package additionaladditions;

import legend.game.additions.Addition;
import legend.game.additions.AdditionRegistryEvent;
import legend.game.additions.CharacterAdditionStats;
import legend.game.additions.UnlockState;
import legend.game.modding.events.gamestate.GameLoadedEvent;
import legend.game.types.CharacterData2c;
import org.legendofdragoon.modloader.Mod;
import org.legendofdragoon.modloader.events.EventListener;
import org.legendofdragoon.modloader.events.Priority;
import org.legendofdragoon.modloader.registries.RegistryId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static legend.core.GameEngine.EVENTS;

@Mod(id = AdditionalAdditionsMod.MOD_ID, version = "3.0.0")
public class AdditionalAdditionsMod {
  public static final String MOD_ID = "additional_additions";

  private final CustomAdditionLoader customAdditionLoader = new CustomAdditionLoader();

  private final Map<RegistryId, List<Addition>> additions = new HashMap<>();

  public AdditionalAdditionsMod() {
    EVENTS.register(this);
  }

  public static RegistryId id(final String entryId) {
    return new RegistryId(MOD_ID, entryId);
  }

  @EventListener(priority = Priority.LOWEST)
  public void registerAdditions(final AdditionRegistryEvent event) {
    this.additions.clear();
    this.additions.putAll(this.customAdditionLoader.loadAllAsAdditions());

    for(final RegistryId charId : CustomAdditionLoader.CHAR_IDS) {
      final List<Addition> additions = this.additions.get(charId);

      if(additions != null) {
        for(int i = 0; i < additions.size(); i++) {
          event.register(id(charId.entryId() + '_' + i), additions.get(i));
        }
      }
    }
  }

  @EventListener
  public void onGameLoaded(final GameLoadedEvent event) {
    for(int i = 0; i < CustomAdditionLoader.CHAR_IDS.size(); i++) {
      final List<Addition> additions = this.additions.get(CustomAdditionLoader.CHAR_IDS.get(i));

      if(additions != null) {
        final CharacterData2c character = event.gameState.charData_32c[i];

        for(final Addition addition : additions) {
          if(!character.additionStats.containsKey(addition.getRegistryId())) {
            final CharacterAdditionStats stats = new CharacterAdditionStats();
            stats.unlockState = UnlockState.UNLOCKED;
            character.additionStats.put(addition.getRegistryId(), stats);
          }
        }
      }
    }
  }
}
