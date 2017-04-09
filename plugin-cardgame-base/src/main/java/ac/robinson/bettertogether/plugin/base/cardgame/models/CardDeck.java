/*
 * Copyright (C) 2017 The Better Together Toolkit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package ac.robinson.bettertogether.plugin.base.cardgame.models;

/**
 * Created by t-sus on 3/23/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ac.robinson.bettertogether.plugin.base.cardgame.R;
import ac.robinson.bettertogether.plugin.base.cardgame.utils.Constants;

public class CardDeck extends Renderable implements CardActions{

    private Context mContext;
    // Mention the entire suite of cards.

    private Bitmap bitmap;
    private boolean hidden;

    private boolean touched;

    private List<Card> mCards;
    private CardDeckType deckType;
    private Integer deckCount = 1;


    // Method to add card to deck.
    public void addCardToDeck(Card mCard) {
        this.mCards.add(mCard);
    }

    public Card removeCardFromDeck(Card card) {
        return mCards.remove(0);
    }
    
    public CardDeck(Context mContext, CardDeckType deckType) {

        this.mContext  = mContext;
        this.deckType = deckType;

        switch (this.deckType){
            case CLOSED:
                this.bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.card_back_stack);
                this.bitmap = Bitmap.createScaledBitmap(this.bitmap, scaledWidth, scaledHeight, true);
                break;
            case OPEN:
                this.bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ace_of_spades);
                this.bitmap = Bitmap.createScaledBitmap(this.bitmap, scaledWidth, scaledHeight, true);
                break;
            case DISCARDED:
                break;
        }

        Random rand = new Random();
        setX(x + rand.nextInt(500) + (bitmap.getWidth()/ 2));
        setY(y + rand.nextInt(1000)+ (bitmap.getHeight()/2));

        this.mCards = new ArrayList<>();

        // TODO currently fixed to one but a deck can have more than one deck of cards
        for (int i = 0, cardId = 1; i < deckCount ; i++) {

            for (Suits suit: Suits.values()) {

                for (CardRank rank: CardRank.values() ) {
                    Card card = new Card();
                    card.setCardId(cardId++);
                    card.setSuit(suit);
                    card.setRank(rank);
                    card.setName(rank + Constants.CONNECTOR + suit);
                    card.setHidden(true);
                    card.setBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                            mContext.getResources().getIdentifier(card.getName(),"drawable",mContext.getPackageName()
                            )));
                    mCards.add(card);
                }
            }
        }

        // TODO Adding the special cards if required

    }

    // Fisher-Yates shuffle

    public String getRandomCardFromDeck() {

//        return mCardDeck.get(mCardValues.get(0)); // Sample
        return "";
    }

    public Card getTopCardFromDeck(Integer deckCode) {

        if( mCards.size() > 0) {
            return mCards.get(0);
        }

        return null;
    }

    public void shuffleCardDeck(List<Card> deck) {
        Collections.shuffle(deck);
    }

    @Override
    public Card drawCard(Integer deckCode, boolean hidden) {
        //TODO hardcoding deck code to 0. deckcode for each deck type
        deckCode = 0;

        Card drawnCard = getTopCardFromDeck(deckCode);

        drawnCard.setHidden(hidden);

        return drawnCard;
    }

    @Override
    public boolean discardCard(Card card) {
        return false;
    }

    @Override
    public boolean showCard(Card card) {
        return false;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y , null);
    }

    @Override
    public boolean isTouched() {
        return this.touched;
    }

    @Override
    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    @Override
    public boolean isOverlapping(Renderable image) {

        if (image.getX() >= (getX() - bitmap.getWidth() ) && (image.getX() <= (getX() + bitmap.getWidth()))) {
            if (image.getY() >= (getY() - bitmap.getHeight() ) && (image.getY() <= (getY() + bitmap.getHeight() ))) {
                Toast.makeText(mContext, "Overlapp Detected !!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return false;
    }

    /**
     * Handles the {@link MotionEvent.ACTION_DOWN} event. If the event happens on the
     * bitmap surface then the touched state is set to <code>true</code> otherwise to <code>false</code>
     * @param eventX - the event's X coordinate
     * @param eventY - the event's Y coordinate
     */
    @SuppressWarnings("JavadocReference")
    public Gesture handleActionDown(int eventX, int eventY) {
        if (eventX >= (getX() - bitmap.getWidth() ) && (eventX <= (getX() + bitmap.getWidth()))) {
            if (eventY >= (getY() - bitmap.getHeight() ) && (eventY <= (getY() + bitmap.getHeight() ))) {
                // droid touched
                if (System.currentTimeMillis() - startTime <= MAX_DURATION) {
                    Toast.makeText(mContext, "Double Tapped on Card !!", Toast.LENGTH_SHORT).show();
                    return Gesture.DOUBLE_TAP;
                }
                setTouched(true);
                startTime = System.currentTimeMillis();
                return Gesture.SINGLE_TAP;
            } else {
                setTouched(false);
            }
        } else {
            setTouched(false);
        }
        return Gesture.NONE;

    }

}
