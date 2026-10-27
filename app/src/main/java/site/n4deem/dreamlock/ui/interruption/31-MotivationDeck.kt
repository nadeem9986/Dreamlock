package site.n4deem.dreamlock.ui.interruption

/** One line of motivation for the interruption screen. Attribution is optional. */
data class MotivationQuote(val text: String, val attribution: String? = null)

/**
 * Deals the quotes like a shuffled deck: every quote shows once before any repeat,
 * and a reshuffle never serves the same quote twice in a row.
 *
 * Bundled in code on purpose: the block screen must work fully offline, with no
 * network call and no loading state standing between the user and the nudge.
 *
 * Voice note: classical lines keep their attribution; lowercase lines with none
 * are Dreamlock speaking in its own terminal voice. No shame, no lecture.
 */
object MotivationDeck {

    private val quotes = listOf(
        // The old guard - public domain, translations of classical sources.
        MotivationQuote("You have power over your mind, not outside events. Realize this, and you will find strength.", "Marcus Aurelius"),
        MotivationQuote("Waste no more time arguing what a good man should be. Be one.", "Marcus Aurelius"),
        MotivationQuote("The impediment to action advances action. What stands in the way becomes the way.", "Marcus Aurelius"),
        MotivationQuote("You could leave life right now. Let that determine what you do and say and think.", "Marcus Aurelius"),
        MotivationQuote("The soul becomes dyed with the color of its thoughts.", "Marcus Aurelius"),
        MotivationQuote("It is not that we have a short time to live, but that we waste a great deal of it.", "Seneca"),
        MotivationQuote("Begin at once to live, and count each separate day as a separate life.", "Seneca"),
        MotivationQuote("While we wait for life, life passes.", "Seneca"),
        MotivationQuote("Luck is what happens when preparation meets opportunity.", "Seneca"),
        MotivationQuote("No man is free who is not master of himself.", "Epictetus"),
        MotivationQuote("First say to yourself what you would be, and then do what you have to do.", "Epictetus"),
        MotivationQuote("How long are you going to wait before you demand the best for yourself?", "Epictetus"),
        MotivationQuote("He who conquers himself is the mightiest warrior.", "Confucius"),
        MotivationQuote("It does not matter how slowly you go, as long as you do not stop.", "Confucius"),
        MotivationQuote("A journey of a thousand miles begins with a single step.", "Laozi"),
        MotivationQuote("Mastering others is strength. Mastering yourself is true power.", "Laozi"),
        MotivationQuote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Will Durant, on Aristotle"),
        MotivationQuote("The best time to plant a tree was twenty years ago. The second best time is now.", "proverb"),
        // Dreamlock's own voice - lowercase, terminal, never a lecture.
        MotivationQuote("the feed will still be there later. this version of you might not."),
        MotivationQuote("you opened this app for a reason. it was not this one."),
        MotivationQuote("your dream does not need more motivation. it needs this hour."),
        MotivationQuote("nobody is coming to do it for you. good thing you are already here."),
        MotivationQuote("scrolling is easy. that is exactly why it cannot be the plan."),
        MotivationQuote("small promise kept daily beats big promise kept never."),
        MotivationQuote("this screen is not a wall. it is a mirror."),
        MotivationQuote("the algorithm has a plan for your attention. do you?"),
        MotivationQuote("one more video becomes one more year. it always does."),
        MotivationQuote("you are not tired of working. you are tired of starting over."),
    )

    private var remaining = mutableListOf<MotivationQuote>()
    private var last: MotivationQuote? = null

    /** Fresh quote for each time the interruption screen appears. */
    @Synchronized
    fun deal(): MotivationQuote {
        if (remaining.isEmpty()) {
            remaining = quotes.shuffled().toMutableList()
            if (remaining.size > 1 && remaining.first() == last) {
                // Swap so a reshuffle never repeats the quote the user just saw.
                val first = remaining[0]
                remaining[0] = remaining[1]
                remaining[1] = first
            }
        }
        return remaining.removeAt(0).also { last = it }
    }
}
