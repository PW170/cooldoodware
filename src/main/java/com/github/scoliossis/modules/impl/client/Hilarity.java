package com.github.scoliossis.modules.impl.client;

import com.github.scoliossis.Main;
import com.github.scoliossis.events.SubscribeEvent;
import com.github.scoliossis.events.impl.KeyPressedEvent;
import com.github.scoliossis.modules.Category;
import com.github.scoliossis.modules.Module;
import com.github.scoliossis.modules.RegisterModule;
import com.github.scoliossis.modules.RegisterSubModule;
import com.github.scoliossis.utils.minecraft.ChatUtil;

// old ahh words list. too lazy to add more
@RegisterModule(
        name = "Hilarity",
        description = "this has NOTHING to do with Hilary Clinton", // description stolen from lesbian hack
        category = Category.CLIENT,
        enabledByDefault = true
)
public class Hilarity extends Module {
    @RegisterSubModule(name = "Slider", description = "you can figure out what this does", min = 1, max = 10000)
    public static int slider = 1000;

    // this does NOTHING!
    @RegisterSubModule(name = "Boolean", description = "im sure this does something")
    public static boolean bool = false;

    private static final String[] hilarityNames = {
            "&c[ADMIN] Jayavarmen",
            "&c[ADMIN] Minikloon",
            "&c[ADMIN] Ladybleu",
            "&c[OWNER] hypixel",
            "&c[OWNER] Rezzus",
            "&c[ADMIN] Plancke",
            "&c[ADMIN] aPunch",
            "&b[MVP&0+&b] Dctr",
            "&c[ADMIN] Thorlon",
            "&c[ADMIN] Froz3n",
            "&c[ADMIN] ChiLynn",
            "&c[Minister] 2nfg",
            "&c[&fYOUTUBE&c] Mushroom",
            "&c[&fYOUTUBE&c] Frozie",
            "&c[&fYOUTUBE&c] K9L",
            "&c[&fYOUTUBE&c] 56ms",
            "&c[&fYOUTUBE&c] AverageSweat",
            "&c[&fYOUTUBE&c] Vegantoes",
            "&c[&fYOUTUBE&c] __Zova",
            "&c[&fYOUTUBE&c] Scale",
            "&c[&fYOUTUBE&c] Alan_Wood",
            "&c[&fYOUTUBE&c] swig",
            "&c[&fYOUTUBE&c] ijustcheat", // rip ijustcheat </3
            "&7kulovhax",
            "&7chazed", // my goat
    };

    private static final String[] hilarityMessages = {
            "your gay\n&dFrom &b[MVP&2+&b] Elg_AF&7: you're*",
            "your getting banned :)\n&dFrom &b[MVP&2+&b] Elg_AF&7: you're*",
            "Your flagging.\n&dFrom &b[MVP&2+&b] Elg_AF&7: you're*",
            "watchdog exists",
            "any last words?",
            "x3 nuzzles! pounces on you uwu you so warm (ooo) Couldn’t help but notice your buldge from across the floor Nuzzles yo' necky wecky-tilda murr-tilda hehe Unzips yo baggy ass pants, oof baby you so musky Take me home, pet me, 'n’ make me yours and don't forget to stuff me! See me wag my widdle baby tail all for your buldgy-wuldgy! Kissies 'n' lickies yo neck I hope daddy likies Nuzzles 'n' wuzzles yo chest (yuh) I be (yeah) gettin' thirsty Hey, I got a little itch, you think you can help me? Only seven inches long uwu PLEASE ADOPT ME Paws on your buldge as I lick my lips (uwu punish me please) 'Boutta hit ’em with this furry shit (he don’t see it comin'). Dowoes youwu lick my sowong.",
            "you really thought that wasnt detectable?",
            "hey whats " + Main.MOD_NAME + "? ;3",
            "du kannst nichts daher ist dein vatter milch hollen",
            "t.me/escamas1337 !!",
            "https://github.com/scoliossis",
            "For those of you that don't know, there are a new pair of boots in the game called Great Spook Boots.\nHypixel, are you honestly not aware that \"Spook\" is a racist slur for a black person? You really couldn't call them \"Spooky\" or \"Halloween\" or \"Pumpkin\" or literally anything else?!\nDon't believe me? Too lazy to google it? Here's the wiki page for the word. Scroll down to Slang uses.\nSlang for someone involved in espionage\nCIA members, in slang\nMI5 members, in slang\nA racial slur for a black person\nA+ patch guys. Really impressed with how little you pay attention to what you put into the game.",

            "@everyone I have decided that I am going to be leaving the Diablo development team for a few reason one of them being tanner, tanner is unable to do simple tasks such as change a variable to stop a task another reason is that the client is unstable and will crash randomly and finally is that no one on the dev team is helpful in anyway I am just left to do most things by myself.",

            "\"Unfortunate\" doesn't begin to describe my series, this game rewards blind luck and nothing else, I am beyond convinced at this point. After getting completely tooled by scheduling with my opponent changing times on me last minute and refusing to provide confirmation prior to the day of the match as to play times, losing this way somehow felt even worse than I had thought possible. My preparation was superior, my play was superior, and I lost, so I don't see a reason to continue engaging in an activity where what is within my control is overwhelmingly outweighed by what is not., " +
                    "I am done with competitive Pokemon, and you won't get a fond farewell. This community is infected to its roots with a degenerative disease that grows stronger over time but stops short of killing its host. Tournaments used to have a competitive spirit at their heart, this has been transplanted and replaced with an artificial organ that feeds on vitriol and mockery from insecure little boys that heckle by the sidelines and tear each other to shreds over scraps of attention. The environment we fostered has trapped us all like this in a vicious cycle, and escaping it requires acceptance of the harshest reality we all scramble to explain away, that none of the countless straining efforts we put ourselves through here will ever amount to one single shining glimmer of significance. I would make this the end, but World Cup is still ongoing, and I would never leave so many great friends out to dry, so I'll suffer through a few more games for them., " +
                    ", " +
                    "One last thing before I leave you all to react with disdain, ridicule, and self-righteous fervor, before you do everything in your power to minimize my words and thoughts, box them up and shove them to some cobwebbed corner of your memory, and hope they disappear forever as a stain on your finite time ground to dust. From this moment on, nothing you say matters to me. The foulest insults you hurl with intent to wound will calmly settle at the earth before my feet, and the venom you spit will bring all the pain of a warm summer breeze. You are less than anything you can conceive, while I carry on, brimming with joy distilled from detachment.",

            "https://www.youtube.com/watch?v=_WABmx5VcyE",

            "Have you checked out mushroom client? Its the FRESHEST blatant client on mushroomer.top. You can save your accounts BLOCK the ban packets. It supports 1.8.9-1.20.4 servers, that means you can play on probably any server you can imagine. It works on windows 10 AND windows 11 as well as maybe others one day, some of you guys can't play on clients because they only work on windows but this one is the same. By default it runs on the mushroom launcher you can also play it in the vanilla minecraft launcher that means you can use your 2FA Microsoft Accounts but if you're fine with using the alt manager in mushroom client that's built-in you can also play on cookie and you can play on all microsoft accounts. This client is undetectable on basically every server and to be honest you can probably configurate it to bypass every server it's got configs came already and some stuff is in the cloud already that means if you play on 1 computer log off join on another computer you're still gonna have all your settings and everything not there., \nChest aura is working just fine.\nLets Godbridge over.\nKillaura.\nKnocked him of the map.\nThere's already someone here, and our ESP tells us the chests have been looted, but that doesn't matter we're gonna kill this dude. awkward cut , \ngayily i can turn up my velocity if you want lil bro. , \nAlright. , \nMan down. , \nLets godbridge over. , \nThe power of mushroom baby. , \nGet em both. Janitor? , \nLets win the game!, \nLets get some armor up gets 2 pieces of armor, \nMan he's, he's already up there. wierd ahh 2 second cut, \nim ready to fight! :mm_devil: , \nLets get up at the top., \nOk he thinks he tnt, you can blow yourself up bro, \n10 seconds of silence?!?!, \nalright!, \nThat's how you do it!, \nSo now there is just one dude left!, \nHe's probably trapping?!, \nSo I'm just gonna do what I'm supposed to., \nOh he dead, \nAlright we win the game!, \nMushroom killaura doin pretty good"
    };

    private static int i = 0;

    // rga does it on a keypressevent, i gotta do the same
    @SubscribeEvent
    public static void doHilaritying(KeyPressedEvent event) {
        if (!event.pressed) return;

        i++;
        if (i < slider) return;

        ChatUtil.chat("&dFrom " + hilarityNames[(int) Math.floor(Math.random() * hilarityNames.length)] + "&7: " + hilarityMessages[(int) Math.floor(Math.random() * hilarityMessages.length)]);
        i = 0;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
