# Simple Parse

Here is better compile-version: https://github.com/yongjhih/auto-parse

## Usage

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
gameScore.commit().saveInBackground();
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
SimpleParse.from(ParseGameScore.class).is(ParseGameScore.PLAYER_NAME, "Dan Stemkoski").find(new FindCallback<ParseGameScore>() {
    public void done(List<ParseGameScore> scoreList, ParseException e) {
        if (e == null) {
            Log.d("score", "Retrieved " + scoreList.size() + " scores");
        } else {
            Log.d("score", "Error: " + e.getMessage());
        }
    }
});
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

Save values via commit():

```java
ParseGameScore gameScore = new ParseGameScore();
gameScore.score = 1337;
gameScore.playerName = "Sean Plott";
gameScore.cheatMode = false;
gameScore.commit().saveInBackground();
```

Application:

```java
    ParseObject.registerSubclass(ParseGameScore.class);
```

other:

```java
SimpleParse.from(ParseGameScore.class).isNot(ParseGameScore.PLAYER_NAME, "Michael Yabuti").findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParse.from(ParseGameScore.class).up(ParseGameScore.PLAYER_AGE, 18).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParse.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParse.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).limit(10).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParse.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).skip(10).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParse.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).descending().findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParse.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).ascending(ParseGameScore.PLAYER_AGE).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
SimpleParse.from(ParseGameScore.class).down(ParseGameScore.PLAYER_WEIGHT, 100).ascending(ParseGameScore.PLAYER_NAME).addAscending(ParseGameScore.PLAYER_AGE).findInBackground(new SimpleFindCallback<ParseGameScore>() {});
```

Bonus
=====

* Import from other non-ParseObject into ParseObject:

```java
public class Profile {
    @ParseColumn
    public String displayName;
}
...
Profile profile = new Profile();
profile.displayName = "Andrew Chen";
SimpleParse.from(profile).saveInBackground(ParseUser.getCurrentUser());
```

* @ParseColumn(prefix = "http://example.com/file/"): prefix string

```java
@ParseColumn(prefix = "http://example.com/file/")
public String picture;
...
gameScore.picture = "andrew_chen.png" // It will be saved as "http://example.com/file/andrew_chen.png" into Parse
gameScore.commit().saveInBackground();
```

* @ParseColumn(filter = MySimpleFilter.class)

```java
@ParseColumn(filter = MySimpleFilter.class)
public String username;
...
gameScore.username = "Andrew Chen";
...
public class MySimpleFilter extends com.parse.simple.SimpleFilter {
    @Override
    public String serialize(String value) {
        return value.toLowerCase();
    }
}
...
gameScore.commit().saveInBackground(); // It will be saved as lowercase "andrew chen" into Parse
```

* @ParseColumn(value = "customColumnName")

```java
@ParseColumn(value = "display_name") // or default column name is field name
public String displayName;
```

See Also
========

* https://parse.com/docs/android_guide#objects
