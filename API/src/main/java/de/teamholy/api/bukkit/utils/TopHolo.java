package de.teamholy.api.bukkit.utils;


/* copyright by Yassino */
public class TopHolo {
/*


    private final Hologram hologram;
    private String currentStatsType = IStatsType.ALLTIME;
    private long cooldown = System.currentTimeMillis();

    private final HashMap<String, HashMap<Integer, String>> tops = new HashMap<>();

    public TopHolo(BukkitHolyAPI instance,String collection,String key, String keyHolo) {
        tops.put(IStatsType.DAILY,new HashMap<>());
        tops.put(IStatsType.MONTHLY,new HashMap<>());
        tops.put(IStatsType.ALLTIME,new HashMap<>());

        hologram = HologramsAPI.createHologram(instance, BukkitHolyAPI.getInstance().getLocationManager().getLocation("topholo"));
        hologram.appendItemLine(new ItemBuilder(Material.DIAMOND_SWORD).build());
        for (int i = 1; i < 15; i++) {
            hologram.appendTextLine("Loading...").setTouchHandler(player -> {
                if (cooldown > System.currentTimeMillis()) {
                    player.sendMessage("§cplease wait!");
                    return;
                }
                cooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2);
                player.playSound(player.getLocation(), Sound.CLICK, 1F, 100F);
                BukkitHolyAPI.getInstance().getDatabaseHandler().getExecutorService().execute(() -> {
                    if (currentStatsType.equalsIgnoreCase(IStatsType.DAILY)) {
                        updateHologram(IStatsType.ALLTIME);
                    } else if (currentStatsType.equalsIgnoreCase(IStatsType.MONTHLY)) {
                        updateHologram(IStatsType.DAILY);
                    } else if (currentStatsType.equalsIgnoreCase(IStatsType.ALLTIME)) {
                        updateHologram(IStatsType.MONTHLY);
                    }
                });
            });
        }

        TextLine clickLine = (TextLine) hologram.getLine(12);
        clickLine.setText("§f§lClick to toggle type!");

        Bukkit.getScheduler().runTaskTimerAsynchronously(instance,() -> {
            tops.put(IStatsType.ALLTIME,refreshTopTen(IStatsType.ALLTIME));
            tops.put(IStatsType.MONTHLY,refreshTopTen(IStatsType.MONTHLY));
            tops.put(IStatsType.DAILY,refreshTopTen(IStatsType.DAILY));
            updateHologram(IStatsType.ALLTIME);
        },1,20*60*15);
    }

    private void updateHologram(String statsType) {
        currentStatsType = statsType;
        TextLine textLine = (TextLine) hologram.getLine(1);
        textLine.setText("§8§m----------§f§lTOP 10§8§m----------");

        for (int j = 2; j <= 11; j++) {
            TopPlayer topPlayer = tops.get(statsType).get(j - 1);
            TextLine playerLine = (TextLine) hologram.getLine(j);
            if (topPlayer != null) {
                playerLine.setText("§7#§6" + (j - 1) + " §8︳ " + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getRankColorWithoutNick(topPlayer.getUuid()) + BukkitHolyAPI.getInstance().getBukkitCloudUtil().getName(topPlayer.getUuid()) + " §8» §a" + topPlayer.getWins() + " Wins");
            } else {
                playerLine.setText("§7-/-");
            }
        }
        TextLine lastLine = (TextLine) hologram.getLine(13);
        if (currentStatsType.equalsIgnoreCase(IStatsType.DAILY)) {
            lastLine.setText("§7Alltime §8︳ §7Monthly §8︳ §e§lDAILY");
            ItemLine itemLine = (ItemLine) hologram.getLine(0);
            itemLine.setItemStack(new ItemBuilder(Material.WOOD_SWORD).build());
        } else if (currentStatsType.equalsIgnoreCase(IStatsType.MONTHLY)) {
            lastLine.setText("§7Alltime §8︳ §e§lMONTHLY §8︳ §7Daily");
            ItemLine itemLine = (ItemLine) hologram.getLine(0);
            itemLine.setItemStack(new ItemBuilder(Material.IRON_SWORD).build());
        } else if (currentStatsType.equalsIgnoreCase(IStatsType.ALLTIME)) {
            lastLine.setText("§e§lALLTIME §8︳ §7Monthly §8︳ §7Daily");
            ItemLine itemLine = (ItemLine) hologram.getLine(0);
            itemLine.setItemStack(new ItemBuilder(Material.DIAMOND_SWORD).build());
        }
        ((TextLine) hologram.getLine(14)).setText("§8§m----------§f§lTOP 10§8§m----------");
    }

    private HashMap<Integer, TopPlayer> refreshTopTen(String statsType) {
        tops.get(statsType).clear();
        StreamSupport.stream()
        List<Document> documentList = BukkitHolyAPI.getInstance().getDatabaseHandler().getMongoDatabase().getCollection(ICollections.MLGRUSH).find().into(Lists.newArrayList());
        HashMap<UUID, Integer> hashMap = new HashMap<>();
        for (Document document : documentList) {
            hashMap.put((UUID) document.get("uuid"), document.get(statsType, Document.class).getInteger("won_games"));
        }
        int rankValue = 0;
        Object[] a = hashMap.entrySet().toArray();
        Arrays.sort(a, (Comparator) (o1, o2) -> ((Map.Entry<UUID, Integer>) o2).getValue().compareTo(((Map.Entry<UUID, Integer>) o1).getValue()));
        HashMap<Integer, TopPlayer> tempMap = new HashMap<>();
        for (Object e : a) {
            rankValue++;
            UUID uuid1 = ((Map.Entry<UUID, Integer>) e).getKey();
            int wins = ((Map.Entry<UUID, Integer>) e).getValue();
            tempMap.put(rankValue,new TopPlayer(wins,uuid1));
            if (rankValue == 10)
                break;
        }

        return tempMap;
    }

    @Getter
    public class TopPlayer {
        private int wins;
        private UUID uuid;

        public TopPlayer(int wins, UUID uuid) {
            this.wins = wins;
            this.uuid = uuid;
        }
    }
*/

}
