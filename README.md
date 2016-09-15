# Java Mongo Util

## Example
```
MongoDataUtil.insertObjectInMongo(object);
```

This method will insert the object in Mongo in a collection named 
after the Class name.
 
```
 mongoUtil.getObjectFromCollection(object.class, hashMap);
```

This method will retrieve the object from Mongo. The first parameter is 
the Class to marshall to and the hash map is a hash map of parameters 
for the mongo Query. ["firstName": "John", "lastName": "Doe"]