import sys
from itertools import combinations

class CardDefs:
    HEARTS = "H"
    SPADES = "S"
    DIAMONDS = "D"
    CLUBS = "C"
    all_suits = [HEARTS, DIAMONDS, SPADES, CLUBS]
    card_ranks = {"A":0, "K":1, "Q":2, "J":3, "T":4, "9":5, "8":6, "7":7, "6":8, "5":9, "4":10, "3":11, "2":12}
    card_ranks_as_str = "AKQJT98765432A" # A's count as high as well as low card for a straight, so it's at both ends of this string

    ROYAL_FLUSH = "ROYAL_FLUSH"
    STRAIGHT_FLUSH = "STRAIGHT_FLUSH"
    FOUR_OF_A_KIND = "FOUR_OF_A_KIND"
    FULL_HOUSE = "FULL_HOUSE"
    FLUSH = "FLUSH"
    STRAIGHT = "STRAIGHT"
    THREE_OF_A_KIND = "THREE_OF_A_KIND"
    TWO_PAIR = "TWO_PAIR"
    ONE_PAIR = "ONE_PAIR"
    HIGH_CARD = "HIGH_CARD"

    poker_ordered_results = {ROYAL_FLUSH:0, STRAIGHT_FLUSH:1, FOUR_OF_A_KIND:2, FULL_HOUSE:3, FLUSH:4, STRAIGHT:5, THREE_OF_A_KIND:6, TWO_PAIR:7, ONE_PAIR:8, HIGH_CARD:9}

class Card:
    def __init__(self, str_rep):
        if len(str_rep) != 2:
            self.usage()
            return

        self.value = self.extract_value(str_rep)
        self.suit = self.extract_suit(str_rep)
        self.rank = CardDefs.card_ranks.get(self.value)

        if (self.suit not in CardDefs.all_suits or self.value not in CardDefs.card_ranks.keys()):
            self.usage()
            return


    def is_better_than(self, other_card):
        return self.rank > other_card.rank

    def extract_value(self, str_rep):
        return str_rep[0:1]  # extract first char

    def extract_suit(self, str_rep):
        return str_rep[1:2]  # extract second char... hmm expected this to work with str_rep[1:1] but in my windows python 2.7.3 interpreter 1:1 returns '', 1:2 returns the second char
    
    def __repr__(self):
        return self.value + self.suit 
     
    def __str__(self):
        return "Value: " + self.value + ", Suit: " + self.suit 

    def usage(self):
        print "Usage: Card('<value><suit>')\n\twhere"
        print "\tvalue is one of: " + ", ".join(CardDefs.card_ranks.keys())
        print "\tsuit is one of: " + ", ".join(CardDefs.all_suits)

# represents the 5 cards in the player's hand
class Hand:
    cards = []
    poker_hand_type = None

    def __init__(self, cards):
        self.cards = cards


    @classmethod
    def from_card_reps(cls, card_reps):
        if card_reps == None or len(card_reps) < 5:
            cls.usage()
            return

        cards = []
        for str_rep in card_reps:
            card = Card(str_rep)
            cards.append(card)

        return cls(cards)

    @classmethod
    def from_cards(cls, cards_arg):
        if cards_arg == None or len(cards_arg) < 5:
            cls.usage()
            return

        return cls(cards_arg)

    @classmethod
    def from_tuple(cls, card_tuple):
        if card_tuple == None or len(card_tuple) < 5:
            cls.usage()
            return

        cards = []
        for t in card_tuple:
            cards.append(t)

        return cls(cards)

    def sort_by_rank(self):
        self.cards.sort(key=lambda x: x.rank)

    def get_card_values_as_string(self):
        ret = ""
        for card in self.cards:
            ret += card.value
        return ret

    def set_hand_type(self, htype):
        self.poker_hand_type = htype

    def get_hand_type(self):
        return self.poker_hand_type

    def is_better_than(self, other_hand):
        if self.poker_hand_type == None or other_hand.poker_hand_type == None:
            raise Exception("Cannot determine better-than - make sure to set poker_hand_type first")

        hand_rank = CardDefs.poker_ordered_results[self.poker_hand_type]
        other_rank = CardDefs.poker_ordered_results[other_hand.poker_hand_type]
        
        if hand_rank < other_rank:
            return True

        if hand_rank == other_rank:
            for hcard in self.cards:
                for ocard in other_hand.cards:
                    if hcard.is_better_than(ocard):
                        return True
        # hand_rank > other_rank or (hand_rank == other_hand_rank and hand card ranks <= other hand card ranks) 
        return False

    @classmethod
    def usage(self):
        print "Usage: Hand(<list of cards>)\n"
        print "\twhere each card is represented by a 2-char code for <value><suit>"
        print "\t eg., Hand(['TH', 'QC', '9H', 'AS', '7D'])"
        print "Note: there has to be atleast 5 cards in a hand for this game"

    def __str__(self):
        return self.cards.__str__()

# represents the top 5 cards in the deck. the deck cards may only be accesses in order - i.e., you may not get the
# 2nd card of the deck if you don't pick the top card first.
class Deck:
    stack = []

    def __init__(self, cards):
        self.stack = cards
        self.stack.reverse() # we want true stack behavior when we pop() from this list

    @classmethod
    def from_card_reps(cls, card_reps):
        if card_reps == None or len(card_reps) < 5:
            cls.usage()
            return

        cards = []
        for str_rep in card_reps:
            card = Card(str_rep)
            cards.append(card)

        return cls(cards)


    def pop_from_deck(self, num_cards_to_pop = 1):
        popped_cards = []
        for k in range(0, num_cards_to_pop):
            popped_cards.append(self.pop())
        return tuple(popped_cards)

    def pop(self):
        return self.stack.pop()


class PsychicPoker:
    deals = []

    def __init__(self, infile):
        f = open(infile, "r") # open read-only
        for line in f:
            in_list = line.split()
            self.deals.append(in_list)


    def play_all_deals(self):
        for deal in self.deals:
            hand_rep = deal[0:5]
            deck_rep = deal[5:10]
            best_hand = self.play_hand(hand_rep, deck_rep)
	    print "Hand:" + " ".join(hand_rep) + " Deck: " + " ".join(deck_rep) + " Best hand: " +best_hand.get_hand_type() + " " + best_hand.__str__()


    def play_hand(self, hand_rep, deck_rep):
        if len(hand_rep) != 5 or len(deck_rep) != 5:
            print "Expecting 5 cards in the hand and 5 on the deck"
            return

        hand = Hand.from_card_reps(hand_rep)
        hand.sort_by_rank()
        deck = Deck.from_card_reps(deck_rep)

        return self.find_best_hand(hand, deck)
    

    def find_best_hand(self, hand, deck):
        # detect best hand in order of definition of what is "best":
        # royal flush, straight flush, four of a kind, full house, flush, straight, three of a kind, two pair, one pair, high card
        
        best_hand = None
        popped_cards = []
        # try all combinations of hand cards, starting with keeping all 5 hand cards,
	# then discarding 1 and picking 1 from the deck, then discarding 2 and picking 2 from the deck, etc...  
        for i in range(5, 0, -1):
            hand_generator = combinations(hand.cards, i) # keeping i cards from the hand, rest from the deck
            num_cards_from_deck = 5 - i

	    if i < 5:
	      popped_cards.append(deck.pop())

            deck_tuple = tuple(popped_cards)

            for hand_tuple in hand_generator:
                gen_hand = hand_tuple + deck_tuple

                new_hand = Hand.from_tuple(gen_hand)
                new_hand.sort_by_rank()

                if (self.is_royal_flush(new_hand)):
                    new_hand.set_hand_type(CardDefs.ROYAL_FLUSH)
		    return new_hand # can't get any better, no need to look further: return this hand as the best
    
                # if not a royal flush, don't return right away, one of the not-yet-checked hands could be better than this

                self.determine_poker_hand_type(new_hand)
    
                if best_hand is None:
                    best_hand = new_hand
                    continue

                if new_hand.is_better_than(best_hand):
                    best_hand = new_hand

        return best_hand

    def determine_poker_hand_type(self, new_hand):
        if (self.is_straight_flush(new_hand)):
            new_hand.set_hand_type(CardDefs.STRAIGHT_FLUSH)
        else:
            top_tuple = self.get_top_n_of_a_kind(new_hand)
            max_card_count = top_tuple[0]
            second_card_count = top_tuple[1]

            if (self.is_four_of_a_kind(max_card_count, second_card_count)):
                new_hand.set_hand_type(CardDefs.FOUR_OF_A_KIND)
            elif (self.is_full_house(max_card_count, second_card_count)):
                new_hand.set_hand_type(CardDefs.FULL_HOUSE)
            elif (self.is_flush(new_hand)):
                new_hand.set_hand_type(CardDefs.FLUSH)
            elif (self.is_straight(new_hand)):
                new_hand.set_hand_type(CardDefs.STRAIGHT)
            elif (self.is_three_of_a_kind(max_card_count, second_card_count)):
                new_hand.set_hand_type(CardDefs.THREE_OF_A_KIND)
            elif (self.is_two_pair(max_card_count, second_card_count)):
                new_hand.set_hand_type(CardDefs.TWO_PAIR)
            elif (self.is_one_pair(max_card_count, second_card_count)):
                new_hand.set_hand_type(CardDefs.ONE_PAIR)
            else: # by default, if not anything else, its a HIGH_CARD hand
                new_hand.set_hand_type(CardDefs.HIGH_CARD)



    # poker hand definition functions
    # assumes sorted hanTH JH QC QD QS QH KH AH 2S 6Sd
    def is_royal_flush(self, hand):
        if (self.is_straight_flush(hand) and hand.cards[0].value == "A"):
            return True
        return False

    # assumes sorted hand
    def is_straight_flush(self, hand):
        if self.is_straight(hand) and self.is_flush(hand):
            return True
        return False 

    def is_four_of_a_kind(self, max_card_count, second_card_count):
        if max_card_count == 4:
            return True
        return False

    def is_full_house(self, max_card_count, second_card_count):
        if max_card_count == 3 and second_card_count == 2:
            return True
        return False

    def is_flush(self, hand):
        prev_suit = None
        for card in hand.cards:
            if prev_suit != None and card.suit != prev_suit:
                return False

            prev_suit = card.suit

        return True

    # assumes sorted hand
    def is_straight(self, hand):
        hand_vals_as_str = hand.get_card_values_as_string()
        if hand_vals_as_str in CardDefs.card_ranks_as_str: # found a straight!
            return True

	# special case: if there is an A in the hand, it got sorted to the top, but it could also match at the low end to form a straight
	rearranged_hand_str = hand_vals_as_str[1:] + hand_vals_as_str[0:1]
        if rearranged_hand_str in CardDefs.card_ranks_as_str: # found a straight!
            return True

        return False

    def is_three_of_a_kind(self, max_card_count, second_card_count):
        if max_card_count == 3 and second_card_count < 2:
            return True
        return False

    def is_two_pair(self, max_card_count, second_card_count):
        if max_card_count == 2 and second_card_count == 2:
            return True
        return False

    def is_one_pair(self, max_card_count, second_card_count):
        if max_card_count == 2 and second_card_count < 2:
            return True
        return False

    def is_high_card(self, hand):
        pass

    # end poker hand definition functions

    # helper functions
    def get_top_n_of_a_kind(self, hand):
        cc = self.get_card_counts(hand)
        max_count = 1
        second_count = 1

        for card_val in cc.keys():
            cc_count = cc.get(card_val)
            if cc_count > max_count:
                max_count = cc_count
            elif cc_count > second_count:
                second_count = cc_count
	print "get_top_n_of_a_kind found counts %d, %d" % (max_count, second_count)
        return (max_count, second_count)


    def get_card_counts(self, hand):
        dcounts = {}
        for card in hand.cards: # initialization loop
            dcounts[card.value] = 0

        for card in hand.cards:
            dcounts[card.value] += 1

        return dcounts


# end PsychicPoker class

if __name__ == "__main__":
    pp = PsychicPoker(sys.argv[1])
    pp.play_all_deals()

