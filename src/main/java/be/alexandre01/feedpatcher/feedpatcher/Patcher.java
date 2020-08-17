package be.alexandre01.feedpatcher.feedpatcher;

import be.alexandre01.feedpatcher.feedpatcher.configs.YamlUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;

public class Patcher extends JavaPlugin implements Listener {
  public HashMap<Player,Integer> isHunger;
  public static YamlUtils yamlUtils;
  public static int slow = 10;
  public int totalPlayers;
  public ArrayList<Enchantment> swordEnchants = new ArrayList<>();
  public ArrayList<Enchantment> bowEnchants = new ArrayList<>();
    public ArrayList<Enchantment> otherEnchant = new ArrayList<>();
    public HashMap<Player,Boolean> anvilInv = new HashMap<>();
    public String welcomeMessage;
    @Override
    public void onEnable() {
        yamlUtils = new YamlUtils(this,"config.yml");
        isHunger = new HashMap<>();
        getServer().getPluginManager().registerEvents(this,this);
        getCommand("feedpatcher").setExecutor(new FeedCommand());
        getCommand("poubelle").setExecutor(new Poubelle());
        for(Player player : Bukkit.getOnlinePlayers()){
            anvilInv.put(player,false);
        }

        if(!yamlUtils.getConfig().contains("slow")){
            yamlUtils.getConfig().set("slow",10);
            yamlUtils.save();
        }
            slow = yamlUtils.getConfig().getInt("join-message");

        if(!yamlUtils.getConfig().contains("join-message")){
            yamlUtils.getConfig().set("join-message","&eBienvenue &6%player%&e sur Octosia ! &7[&6%n%&7]");
            yamlUtils.save();
        }

        welcomeMessage = yamlUtils.getConfig().getString("join-message");

        totalPlayers = Bukkit.getOfflinePlayers().length;

        //CHECK ENCHANTS
        ArrayList<Integer> in = new ArrayList<>();
        for (int i = 0; i < Enchantment.values().length; i++) {
            in.add(i);

            if(Enchantment.values()[i].canEnchantItem(new ItemStack(Material.DIAMOND_SWORD))){
            swordEnchants.add(Enchantment.values()[i]);
            System.out.println("SWORD => " + Enchantment.values()[i] );
            }else {
                if(Enchantment.values()[i].canEnchantItem(new ItemStack(Material.BOW))){
                    bowEnchants.add(Enchantment.values()[i]);
                    System.out.println("BOW => " + Enchantment.values()[i] );
                }else {
                    otherEnchant.add(Enchantment.values()[i]);
                }
            }





            swordEnchants.remove(Enchantment.KNOCKBACK);
            bowEnchants.remove(Enchantment.ARROW_KNOCKBACK);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public void onPotionSplash(PotionSplashEvent event){


          for(PotionEffect e : event.getPotion().getEffects()){
              if(e.getType().equals(PotionEffectType.INCREASE_DAMAGE)){
                  if(e.getAmplifier() > 0){
                      event.setCancelled(true);

                     for(LivingEntity l : event.getAffectedEntities()){
                         l.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,e.getDuration(),0),true);
                     }

                  }
              }if(e.getType().equals(PotionEffectType.POISON)){
                  event.setCancelled(true);
                  for(LivingEntity l : event.getAffectedEntities()){
                      int duration = e.getDuration();
                      if(duration > 45*20){
                          duration = 45*20;
                      }
                      l.addPotionEffect(new PotionEffect(PotionEffectType.POISON,duration,0),true);
                  }
              }
              if(e.getType().equals(PotionEffectType.SLOW)){
                  event.setCancelled(true);
                  for(LivingEntity l : event.getAffectedEntities()){
                      int duration = e.getDuration();
                      if(duration > 45*20){
                          duration = 45*20;
                      }
                      l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,duration,0),true);
                  }
              }
              if(e.getType().equals(PotionEffectType.WEAKNESS)){
                  event.setCancelled(true);
                  for(LivingEntity l : event.getAffectedEntities()){
                      int duration = e.getDuration();
                      if(duration > 45*20){
                          duration = 45*20;
                      }
                      l.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,duration,0),true);
                  }
              }
              if(e.getType().equals(PotionEffectType.HARM)){
                  event.setCancelled(true);
              }
              if(e.getType().equals(PotionEffectType.INVISIBILITY)){
                  event.setCancelled(true);
              }
          }



    }


    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent event){
        if(event.getItem().getType().equals(Material.POTION)){
            PotionMeta p = (PotionMeta) event.getItem().getItemMeta();
            Collection<PotionEffect> fx = Potion.fromItemStack(event.getItem()).getEffects();
            for (PotionEffect e : fx){
                if(e.getType().equals(PotionEffectType.INCREASE_DAMAGE)){

                        if(e.getAmplifier() > 0){
                        event.setCancelled(true);
                        event.setItem(new ItemStack(Material.AIR));
                        event.getPlayer().setItemInHand(new ItemStack(Material.GLASS_BOTTLE));
                        event.getPlayer().updateInventory();
                        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,e.getDuration(),0),true);

                    }
                }
                if(e.getType().equals(PotionEffectType.POISON)){
                    event.setCancelled(true);
                    event.setItem(new ItemStack(Material.AIR));
                    event.getPlayer().setItemInHand(new ItemStack(Material.GLASS_BOTTLE));
                    event.getPlayer().updateInventory();
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,e.getDuration(),0),true);
                }

                if(e.getType().equals(PotionEffectType.INVISIBILITY)){
                    event.setCancelled(true);
                    event.getPlayer().setItemInHand(new ItemStack(Material.GLASS_BOTTLE));
                    event.getPlayer().updateInventory();
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,e.getDuration(),0),true);
                    event.getPlayer().sendMessage("§cCette potion est interdite à l'usage");
                }
            }



        }


    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInvOpen(InventoryOpenEvent event){

        if(event.getInventory().getType().equals(InventoryType.ANVIL)){
            anvilInv.put((Player)event.getPlayer(),true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInvClose(InventoryCloseEvent event){
        if(event.getInventory().getType().equals(InventoryType.ANVIL)){
            if(anvilInv.containsKey((Player)event.getPlayer())){
                anvilInv.put((Player)event.getPlayer(),false);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onAnvil(InventoryClickEvent event){
        if(anvilInv.get((Player)event.getWhoClicked())){

        if(event.getCurrentItem() != null){


        if(event.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)event.getCurrentItem().getItemMeta();

            if(meta.getStoredEnchants().containsKey(Enchantment.KNOCKBACK)){
                meta.removeStoredEnchant(Enchantment.KNOCKBACK);

                Random rand = new Random();
               meta.addStoredEnchant(swordEnchants.get(rand.nextInt(swordEnchants.size())),1,false);
               event.getCurrentItem().setItemMeta(meta);
                ((Player)event.getWhoClicked()).updateInventory();
                event.getWhoClicked().sendMessage("§cL'enchantement KNOCKBACK est désactivé, l'enchantement à été remplacé par un autre.");
            }

            if(meta.getStoredEnchants().containsKey(Enchantment.ARROW_KNOCKBACK)){
                meta.removeStoredEnchant(Enchantment.ARROW_KNOCKBACK);
                Random rand = new Random();
                meta.addStoredEnchant(bowEnchants.get(rand.nextInt(bowEnchants.size())),1,false);
                event.getCurrentItem().setItemMeta(meta);
                ((Player)event.getWhoClicked()).updateInventory();
                event.getWhoClicked().sendMessage("§cL'enchantement PUNCH est désactivé, l'enchantement à été remplacé par un autre.");
            }

        }
        }

        }
    }
    @EventHandler
    public void onEnchant(EnchantItemEvent event){

        Map<Enchantment, Integer> enchantsToAdd = event.getEnchantsToAdd();
        if (enchantsToAdd.containsKey(Enchantment.KNOCKBACK)) {
            event.getEnchantsToAdd().remove(Enchantment.KNOCKBACK);
            Random rand = new Random();
            event.getEnchantsToAdd().put(swordEnchants.get(rand.nextInt(swordEnchants.size())),1);
            event.getEnchanter().sendMessage("§cL'enchantement KNOCKBACK est désactivé, l'enchantement à été remplacé par un autre.");
        } else if (enchantsToAdd.containsKey(Enchantment.ARROW_KNOCKBACK)) {
            event.getEnchantsToAdd().remove(Enchantment.ARROW_KNOCKBACK);
            Random rand = new Random();
            event.getEnchantsToAdd().put(bowEnchants.get(rand.nextInt(bowEnchants.size())),1);
            event.getEnchanter().sendMessage("§cL'enchantement PUNCH est désactivé, l'enchantement à été remplacé par un autre.");
        }







    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(!event.getPlayer().hasPlayedBefore()){
            totalPlayers++;
            Bukkit.broadcastMessage(welcomeMessage.replaceAll("%player%",event.getPlayer().getName()).replaceAll("%n%", String.valueOf(totalPlayers)).replaceAll("&","§"));
        }
        anvilInv.put(event.getPlayer(),false);



    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        anvilInv.remove(event.getPlayer());
    }
    @EventHandler
    public void onFeed(FoodLevelChangeEvent event){

        if(event.getEntity() instanceof Player){

            Player player = (Player) event.getEntity();
            if(event.getFoodLevel() < player.getFoodLevel()){
            if(isHunger.containsKey(player)){

                if(isHunger.get(player) == slow){
                    isHunger.put(player,0);

                }else {
                    event.setCancelled(true);
                    isHunger.put(player,isHunger.get(player)+1);
                }
            }else {
                isHunger.put(player,0);
            }
        }

    }
    }
    /*@EventHandler
    public void onCraft(EnchantItemEvent event){
        if(event.getEnchantsToAdd().containsKey(Enchantment.KNOCKBACK)){
            event.getEnchantsToAdd().remove(Enchantment.KNOCKBACK);
            event.getEnchantsToAdd().put()
        }
    }*/

  public static void setFinalStatic(Field field, Object newValue) throws Exception {
                               field.setAccessible(true);

                               Field modifiersField = Field.class.getDeclaredField("modifiers");
                               modifiersField.setAccessible(true);
                               modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);

                               field.set(null, newValue);
  }
  }