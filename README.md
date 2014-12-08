Simple Parse
============

Usage
=====

A Type

```java
@ParseObjectSubclass("User")
public interface IParseUser {
  @ParseColumn("facebookId")
  public String getFacebookId(); // ParseObject.getString("facebookId")

  public IParseUser setFacebookId(@ParseColumn("facebookId") String facebookId); // ParseObject.putString("facebookId", facebookId)
}

SimpleParseQuery.from(IParseUser.class).is("xWMyZ4YEGZ").query(new GetCallback<ParseUser>() {
  public void done(ParseUser parseUser, ParseException e) {
    if (e == null) {
      parseUser.getFacebookId();
    }
  }
});

SimpleParseQuery.from(IParseUser.class).setFacebookId("123").saveInBackground();
```

B Type

```java
@ParseObjectSubclass("User")
public class ParseUser extends SimpleParseUser // extends SimpleParseObject {
  @ParseColumn("facebookId")
  public String mFacebookId;

  public ParseUser setFacebookId(String facebookId) {
    mFacebookId = facebookId;
    return this;
  }

  public ParseUser getFacebookId() {
    return mFacebookId;
  }
}

new ParseUser().setFacebookId("123").saveInBackground();

SimpleParseQuery.from(ParseUser.class).is("xWMyZ4YEGZ").query(new GetCallback<ParseUser>() {
  public void done(ParseUser parseUser, ParseException e) {
    if (e == null) {
      parseUser.getFacebookId();
    }
  }
});
```
