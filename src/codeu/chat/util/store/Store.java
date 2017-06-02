// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.util.store;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

//import codeu.chat.common.User;

public class Store<KEY, VALUE> implements StoreAccessor<KEY, VALUE> {

  // To make the code simpler - use a dummy link for the first link in this
  // list. The root link is never read from. To avoid reading from this link
  // the "next" value is used more than the "this" or "current" reference.
  private final StoreLink<KEY, VALUE> rootLink = new StoreLink<>(null, null, null);

  private final NavigableMap<KEY, StoreLink<KEY, VALUE>> index;

  private final Comparator<KEY> comparator;

  public Store(Comparator<KEY> comparator) {
    this.index = new TreeMap<>(comparator);
    this.comparator = comparator;
  }

  public void insert(KEY key, VALUE value) {

    final StoreLink<KEY, VALUE> closestLink = floor(key);

    // Assume that the new value can only come after the current position. Move
    // through the chain of links until the next link is either the end (null)
    // or will logically come after the new value.
    StoreLink<KEY, VALUE> current = (closestLink == null) ? (rootLink) : (closestLink);
    while(current.next != null && comparator.compare(current.next.key, key) <= 0) {
      current = current.next;
    }

    // "current.next" may be null, but "current" can never be null. So it
    // should always be safe to call to current.
    final StoreLink<KEY, VALUE> newLink = new StoreLink<>(key, value, current.next);
    current.next = newLink;

    // Before adding the link to the index, first check if the hint has an
    // equal key. If it does - do not add the index.
    //
    // There are two reasons for this:
    //  1. The index class does not handle duplicate keys well (as stated in
    //     the code for the index).
    //  2. There is no advantage to having multiple equal keys in the index as
    //     it would not help with the interators. As long as the key will map
    //     to the first link, the other links will always be found. This is
    //     why the insert is always put at the end of the series.
    
    if (closestLink == null || comparator.compare(newLink.key, closestLink.key) != 0) {
      index.put(key, newLink);
    }
  }



  /*
  * Removes the StoreLink corresponding to the provided key from the Store
  *
  * Uses TreeMap's remove method to remove the StoreLink corresponding to the provided key from
  * the Store. Checks if the key was successfully removed and prints message.
  *
  * @param key key of StoreLink to remove from Store
  */
  //remove method for Store that uses the TreeMap's remove method and ensures that a value was removed
  public void remove(KEY key){
    StoreLink<KEY, VALUE> v; 
    
    StoreLink<KEY, VALUE> linkBefore = extract(index.lowerEntry(key)); 
    
    // Check if linkBefore is null, if so then we know the previous link is the root link, so set accordingly 
    if(linkBefore==null){
      linkBefore = rootLink; 
    }

    // updates links so Store remains intact after removal
    StoreLink<KEY, VALUE> linkToDelete = index.get(key); 
    StoreLink<KEY, VALUE> linkAfter = linkToDelete.next;

    linkBefore.next = linkAfter;

  	if((v=index.remove(key))!=null){
  	  //Success
  	  System.out.println(v + " was removed successfully."); 
  	} else {
  	  System.out.println(v + " was not removed successfully. There was an issue."); 
  	} 
  }

  /*
  * Checks whether a key exists in the Store.
  *
  * Given a key, uses TreeMap's get method to determine if key exists within the Store.
  * Returns a boolean stating whether the key exists in the store.
  *
  * @param key key to find in Store
  * @return boolean stating whether key exists in Store
  */
  //exist method for Store that uses the TreeMap's get method and ensures that a value exists
  public boolean exists(KEY key){
    boolean userExists = (index.get(key)!=null);  
  	return userExists; 
  }

  @Override
  public VALUE first(KEY key) {
    final StoreLink<KEY, VALUE> link = index.get(key);
    return link == null ? null : link.value;
  }

  @Override
  public Iterable<VALUE> all() {
    return new LinkIterable<KEY, VALUE>(comparator, first(), last());
  }

  @Override
  public Iterable<VALUE> at(final KEY key) {
    return new LinkIterable<KEY, VALUE>(comparator, ceiling(key), floor(key));
  }

  @Override
  public Iterable<VALUE> after(KEY start) {
    return new LinkIterable<KEY, VALUE>(comparator, ceiling(start), last());
  }

  @Override
  public Iterable<VALUE> before(KEY end) {
    return new LinkIterable<KEY, VALUE>(comparator, first(), floor(end));
  }

  @Override
  public Iterable<VALUE> range(KEY start, KEY end) {
    return new LinkIterable<KEY, VALUE>(comparator, ceiling(start), floor(end));
  }

  private StoreLink<KEY, VALUE> first() {
    return extract(index.firstEntry());
  }

  private StoreLink<KEY, VALUE> last() {
    return extract(index.lastEntry());
  }

  private StoreLink<KEY, VALUE> ceiling(KEY key) {
    return extract(index.ceilingEntry(key));
  }

  private StoreLink<KEY, VALUE> floor(KEY key) {
    return extract(index.floorEntry(key));
  }

  private StoreLink<KEY, VALUE> extract(Map.Entry<KEY, StoreLink<KEY, VALUE>> entry) {
    return entry == null ? null : entry.getValue();
  }
}
