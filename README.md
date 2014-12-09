Simple Parse
============

before:

```java
ParseObject gameScore = new ParseObject("GameScore");
gameScore.put("score", 1337);
gameScore.put("playerName", "Sean Plott");
gameScore.put("cheatMode", false);
gameScore.saveInBackground();
```

after:

```java
ParseGameScore gameScore = new ParseGameScore();
gameScore.score = 1337;
gameScore.playerName = "Sean Plott";
gameScore.cheatMode = false;
gameScore.saveInBackground(); //SimpleParseQuery.from(ParseGameScore.class).saveInBackground(gameScore);
```

before:

```java
ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
query.whereEqualTo("playerName", "Dan Stemkoski");
query.findInBackground(new FindCallback<ParseObject>() {
    public void done(List<ParseObject> scoreList, ParseException e) {
        if (e == null) {
            Log.d("score", "Retrieved " + scoreList.size() + " scores");
        } else {
            Log.d("score", "Error: " + e.getMessage());
        }
    }
});
```

after:

```java
SimpleParseQuery.from(ParseGameScore.class).is(ParseGameScore.PLAYER_NAME, "Dan Stemkoski").findInBackground(new FindCallback<ParseGameScore>() {
    public void done(List<ParseGameScore> scoreList, ParseException e) {
        if (e == null) {
            Log.d("score", "Retrieved " + scoreList.size() + " scores");
        } else {
            Log.d("score", "Error: " + e.getMessage());
        }
    }
});
```

other after:

```java
SimpleParseQuery.from(ParseGameScore.class).isNot(ParseGameScore.PLAYER_NAME, "Michael Yabuti").findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParseQuery.from(ParseGameScore.class).up(ParseGameScore.PLAYER_AGE, 18).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParseQuery.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParseQuery.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).limit(10).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParseQuery.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).skip(10).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParseQuery.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).descending().findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParseQuery.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).ascending(ParseGameScore.PLAYER_AGE).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParseQuery.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).ascending(ParseGameScore.PLAYER_NAME).addAscending(ParseGameScore.PLAYER_AGE).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
```

Usage
=====

ParseGameScore.java:

```java
@ParseClassName("GameScore")
public class ParseGameScore extends SimpleParseObject {
    @ParseColumn
    public int score;

    @ParseColumn
    public String playerName;

    @ParseColumn
    public boolean cheatMode;
}
```

Application:

```java
    SimpleParseObject.registerSubclass(ParseGameScore.class);
```

See Also
========

* https://parse.com/docs/android_guide#objects
