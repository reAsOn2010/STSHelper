package stshelper;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import stshelper.server.Server;

import java.util.ArrayList;
import java.util.LinkedList;

public class MyHelper {

    static private Server server = new Server();

    static private LinkedList<AbstractCard> buffer = new LinkedList<AbstractCard>();

    static public void startServer() {
        System.out.println("server is running!");
    }

    static public void returnCards(ArrayList<AbstractCard> original) {
        if (buffer.isEmpty()) {
            return;
        }
        System.out.println("---original---");
        for (AbstractCard c: original) {
            System.out.println(CardLibrary.getCard(c.cardID));
        }
        System.out.println("--- ---");
        AbstractCard target = getCard();
        boolean existed = false;
        for (AbstractCard card: original) {
            if (card.cardID.equals(target.cardID)) {
                existed = true;
                break;
            }
        }
        if (!existed) {
            boolean isChanged = false;
            for (AbstractCard card: original) {
                if (card.rarity == target.rarity) {
                    // if (card.upgraded) {
                    //     target.upgrade();
                    // }
                    target.upgrade();
                    original.remove(card);
                    original.add(target);
                    isChanged = true;
                    break;
                }
            }
            if (!isChanged) {
                AbstractCard card = original.remove(0);
                // if (card.upgraded) {
                //     target.upgrade();
                // }
                target.upgrade();
                original.add(target);
            }
        }
        for (AbstractCard c: original) {
            System.out.println(CardLibrary.getCard(c.cardID));
        }
        System.out.println("---hacked---");
    }

    static private AbstractCard getCard() {
        synchronized (buffer) {
            return buffer.remove();
        }
    }

    static public void putCard(AbstractCard card) {
        synchronized (buffer) {
            buffer.offer(card);
        }
    }
}