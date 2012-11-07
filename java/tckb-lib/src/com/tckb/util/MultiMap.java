/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

// 
// Copyright 2004-2005 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
/* ------------------------------------------------------------ */

//
//Copyright 2004-2005 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//
/* ------------------------------------------------------------ */
/** A multi valued Map.
 * This Map specializes HashMap and provides methods
 * that operate on multi valued items. 
 * <P>
 * Implemented as a map of LazyList values
 *
 * @see LazyList
 * @author Greg Wilkins (gregw)
 */
public class MultiMap extends HashMap
        implements Cloneable {
    /* ------------------------------------------------------------ */

    /** Constructor. 
     */
    public MultiMap() {
    }

    /* ------------------------------------------------------------ */
    /** Constructor. 
     * @param size Capacity of the map
     */
    public MultiMap(int size) {
        super(size);
    }

    /* ------------------------------------------------------------ */
    /** Constructor. 
     * @param map 
     */
    public MultiMap(Map map) {
        super((map.size() * 3) / 2);
        putAll(map);
    }

    /* ------------------------------------------------------------ */
    /** Get multiple values.
     * Single valued entries are converted to singleton lists.
     * @param name The entry key. 
     * @return Unmodifieable List of values.
     */
    public List getValues(Object name) {
        return LazyList.getList(super.get(name), true);
    }

    /* ------------------------------------------------------------ */
    /** Get a value from a multiple value.
     * If the value is not a multivalue, then index 0 retrieves the
     * value or null.
     * @param name The entry key.
     * @param i Index of element to get.
     * @return Unmodifieable List of values.
     */
    public Object getValue(Object name, int i) {
        Object l = super.get(name);
        if (i == 0 && LazyList.size(l) == 0) {
            return null;
        }
        return LazyList.get(l, i);
    }

    /* ------------------------------------------------------------ */
    /** Get value as String.
     * Single valued items are converted to a String with the toString()
     * Object method. Multi valued entries are converted to a comma separated
     * List.  No quoting of commas within values is performed.
     * @param name The entry key. 
     * @return String value.
     */
    public String getString(Object name) {
        Object l = super.get(name);
        switch (LazyList.size(l)) {
            case 0:
                return null;
            case 1:
                Object o = LazyList.get(l, 0);
                return o == null ? null : o.toString();
            default:
                StringBuffer values = new StringBuffer(128);
                synchronized (values) {
                    for (int i = 0; i < LazyList.size(l); i++) {
                        Object e = LazyList.get(l, i);
                        if (e != null) {
                            if (values.length() > 0) {
                                values.append(',');
                            }
                            values.append(e.toString());
                        }
                    }
                    return values.toString();
                }
        }
    }

    /* ------------------------------------------------------------ */
    public Object get(Object name) {
        Object l = super.get(name);
        switch (LazyList.size(l)) {
            case 0:
                return null;
            case 1:
                Object o = LazyList.get(l, 0);
                return o;
            default:
                return LazyList.getList(l, true);
        }
    }

    /* ------------------------------------------------------------ */
    /** Put and entry into the map.
     * @param name The entry key. 
     * @param value The entry value.
     * @return The previous value or null.
     */
    public Object put(Object name, Object value) {
        return super.put(name, LazyList.add(null, value));
    }

    /* ------------------------------------------------------------ */
    /** Put multi valued entry.
     * @param name The entry key. 
     * @param values The List of multiple values.
     * @return The previous value or null.
     */
    public Object putValues(Object name, List values) {
        return super.put(name, values);
    }

    /* ------------------------------------------------------------ */
    /** Put multi valued entry.
     * @param name The entry key. 
     * @param values The String array of multiple values.
     * @return The previous value or null.
     */
    public Object putValues(Object name, String[] values) {
        Object list = null;
        for (int i = 0; i < values.length; i++) {
            list = LazyList.add(list, values[i]);
        }
        return put(name, list);
    }

    /* ------------------------------------------------------------ */
    /** Add value to multi valued entry.
     * If the entry is single valued, it is converted to the first
     * value of a multi valued entry.
     * @param name The entry key. 
     * @param value The entry value.
     */
    public void add(Object name, Object value) {
        Object lo = super.get(name);
        Object ln = LazyList.add(lo, value);
        if (lo != ln) {
            super.put(name, ln);
        }
    }

    /* ------------------------------------------------------------ */
    /** Add values to multi valued entry.
     * If the entry is single valued, it is converted to the first
     * value of a multi valued entry.
     * @param name The entry key. 
     * @param values The List of multiple values.
     */
    public void addValues(Object name, List values) {
        Object lo = super.get(name);
        Object ln = LazyList.addCollection(lo, values);
        if (lo != ln) {
            super.put(name, ln);
        }
    }

    /* ------------------------------------------------------------ */
    /** Add values to multi valued entry.
     * If the entry is single valued, it is converted to the first
     * value of a multi valued entry.
     * @param name The entry key. 
     * @param values The String array of multiple values.
     */
    public void addValues(Object name, String[] values) {
        Object lo = super.get(name);
        Object ln = LazyList.addCollection(lo, Arrays.asList(values));
        if (lo != ln) {
            super.put(name, ln);
        }
    }

    /* ------------------------------------------------------------ */
    /** Remove value.
     * @param name The entry key. 
     * @param value The entry value. 
     * @return true if it was removed.
     */
    public boolean removeValue(Object name, Object value) {
        Object lo = super.get(name);
        Object ln = lo;
        int s = LazyList.size(lo);
        if (s > 0) {
            ln = LazyList.remove(lo, value);
            if (ln == null) {
                super.remove(name);
            } else {
                super.put(name, ln);
            }
        }
        return LazyList.size(ln) != s;
    }

    /* ------------------------------------------------------------ */
    /** Put all contents of map.
     * @param m Map
     */
    public void putAll(Map m) {
        Iterator i = m.entrySet().iterator();
        boolean multi = m instanceof MultiMap;
        while (i.hasNext()) {
            Map.Entry entry =
                    (Map.Entry) i.next();
            if (multi) {
                super.put(entry.getKey(), LazyList.clone(entry.getValue()));
            } else {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /* ------------------------------------------------------------ */
    /** 
     * @return Map of String arrays
     */
    public Map toStringArrayMap() {
        HashMap map = new HashMap(size() * 3 / 2);

        Iterator i = super.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            Object l = entry.getValue();
            String[] a = LazyList.toStringArray(l);
            // for (int j=a.length;j-->0;)
            //    if (a[j]==null)
            //         a[j]="";
            map.put(entry.getKey(), a);
        }
        return map;
    }

    /* ------------------------------------------------------------ */
    public Object clone() {
        MultiMap mm = (MultiMap) super.clone();

        Iterator iter = mm.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            entry.setValue(LazyList.clone(entry.getValue()));
        }

        return mm;
    }
}
/** Lazy List creation.
 * A List helper class that attempts to avoid unneccessary List
 * creation.   If a method needs to create a List to return, but it is
 * expected that this will either be empty or frequently contain a
 * single item, then using LazyList will avoid additional object
 * creations by using Collections.EMPTY_LIST or
 * Collections.singletonList where possible.
 *
 * <p><h4>Usage</h4>
 * <pre>
 *   Object lazylist =null;
 *   while(loopCondition)
 *   {
 *     Object item = getItem();
 *     if (item.isToBeAdded())
 *         lazylist = LazyList.add(lazylist,item);
 *   }
 *   return LazyList.getList(lazylist);
 * </pre>
 *
 * An ArrayList of default size is used as the initial LazyList.
 *
 * @see java.util.List
 * @author Greg Wilkins (gregw)
 */
class LazyList
        implements Cloneable, Serializable {

    private static final String[] __EMTPY_STRING_ARRAY = new String[0];

    /* ------------------------------------------------------------ */
    /** Add an item to a LazyList 
     * @param list The list to add to or null if none yet created.
     * @param item The item to add.
     * @return The lazylist created or added to.
     */
    public static Object add(Object list, Object item) {
        if (list == null) {
            if (item instanceof List || item == null) {
                List l = new ArrayList();
                l.add(item);
                return l;
            }

            return item;
        }

        if (list instanceof List) {
            ((List) list).add(item);
            return list;
        }

        List l = new ArrayList();
        l.add(list);
        l.add(item);
        return l;
    }

    /* ------------------------------------------------------------ */
    private LazyList() {
    }

    /* ------------------------------------------------------------ */
    /** Add an item to a LazyList 
     * @param list The list to add to or null if none yet created.
     * @param index The index to add the item at.
     * @param item The item to add.
     * @return The lazylist created or added to.
     */
    public static Object add(Object list, int index, Object item) {
        if (list == null) {
            if (index > 0 || item instanceof List || item == null) {
                List l = new ArrayList();
                l.add(index, item);
                return l;
            }
            return item;
        }

        if (list instanceof List) {
            ((List) list).add(index, item);
            return list;
        }

        List l = new ArrayList();
        l.add(list);
        l.add(index, item);
        return l;
    }

    /* ------------------------------------------------------------ */
    /** Add the contents of a Collection to a LazyList
     * @param list The list to add to or null if none yet created.
     * @param collection The Collection whose contents should be added.
     * @return The lazylist created or added to.
     */
    public static Object addCollection(Object list, Collection collection) {
        Iterator i = collection.iterator();
        while (i.hasNext()) {
            list = LazyList.add(list, i.next());
        }
        return list;
    }

    /* ------------------------------------------------------------ */
    /** Add the contents of an array to a LazyList
     * @param list The list to add to or null if none yet created.
     * @param collection The Collection whose contents should be added.
     * @return The lazylist created or added to.
     */
    public static Object addArray(Object list, Object[] array) {
        for (int i = 0; array != null && i < array.length; i++) {
            list = LazyList.add(list, array[i]);
        }
        return list;
    }

    /* ------------------------------------------------------------ */
    /** Ensure the capcity of the underlying list.
     * 
     */
    public static Object ensureSize(Object list, int initialSize) {
        if (list == null) {
            return new ArrayList(initialSize);
        }
        if (list instanceof ArrayList) {
            ArrayList ol = (ArrayList) list;
            if (ol.size() > initialSize) {
                return ol;
            }
            ArrayList nl = new ArrayList(initialSize);
            nl.addAll(ol);
            return nl;
        }
        List l = new ArrayList(initialSize);
        l.add(list);
        return l;
    }

    /* ------------------------------------------------------------ */
    public static Object remove(Object list, Object o) {
        if (list == null) {
            return null;
        }

        if (list instanceof List) {
            List l = (List) list;
            l.remove(o);
            if (l.isEmpty()) {
                return null;
            }
            return list;
        }

        if (list.equals(o)) {
            return null;
        }
        return list;
    }

    /* ------------------------------------------------------------ */
    public static Object remove(Object list, int i) {
        if (list == null) {
            return null;
        }

        if (list instanceof List) {
            List l = (List) list;
            l.remove(i);
            if (l.isEmpty()) {
                return null;
            }
            return list;
        }

        if (i == 0) {
            return null;
        }
        return list;
    }

    /* ------------------------------------------------------------ */
    /** Get the real List from a LazyList.
     * 
     * @param list A LazyList returned from LazyList.add(Object)
     * @return The List of added items, which may be an EMPTY_LIST
     * or a SingletonList.
     */
    public static List getList(Object list) {
        return getList(list, false);
    }


    /* ------------------------------------------------------------ */
    /** Get the real List from a LazyList.
     * 
     * @param list A LazyList returned from LazyList.add(Object) or null
     * @param nullForEmpty If true, null is returned instead of an
     * empty list.
     * @return The List of added items, which may be null, an EMPTY_LIST
     * or a SingletonList.
     */
    public static List getList(Object list, boolean nullForEmpty) {
        if (list == null) {
            return nullForEmpty ? null : Collections.EMPTY_LIST;
        }
        if (list instanceof List) {
            return (List) list;
        }

        List l = new ArrayList(1);
        l.add(list);
        return l;
    }

    /* ------------------------------------------------------------ */
    public static String[] toStringArray(Object list) {
        if (list == null) {
            return __EMTPY_STRING_ARRAY;
        }

        if (list instanceof List) {
            List l = (List) list;
            String[] a = new String[l.size()];
            for (int i = l.size(); i-- > 0;) {
                Object o = l.get(i);
                if (o != null) {
                    a[i] = o.toString();
                }
            }
            return a;
        }

        return new String[]{list.toString()};
    }

    /* ------------------------------------------------------------ */
    public static Object toArray(Object list, Class aClass) {
        if (list == null) {
            return (Object[]) Array.newInstance(aClass, 0);
        }

        if (list instanceof List) {
            List l = (List) list;
            if (aClass.isPrimitive()) {
                Object a = Array.newInstance(aClass, l.size());
                for (int i = 0; i < l.size(); i++) {
                    Array.set(a, i, l.get(i));
                }
                return a;
            }
            return l.toArray((Object[]) Array.newInstance(aClass, l.size()));

        }

        Object a = Array.newInstance(aClass, 1);
        Array.set(a, 0, list);
        return a;
    }

    /* ------------------------------------------------------------ */
    /** The size of a lazy List 
     * @param list  A LazyList returned from LazyList.add(Object) or null
     * @return the size of the list.
     */
    public static int size(Object list) {
        if (list == null) {
            return 0;
        }
        if (list instanceof List) {
            return ((List) list).size();
        }
        return 1;
    }

    /* ------------------------------------------------------------ */
    /** Get item from the list 
     * @param list  A LazyList returned from LazyList.add(Object) or null
     * @param i int index
     * @return the item from the list.
     */
    public static Object get(Object list, int i) {
        if (list == null) {
            throw new IndexOutOfBoundsException();
        }

        if (list instanceof List) {
            return ((List) list).get(i);
        }

        if (i == 0) {
            return list;
        }

        throw new IndexOutOfBoundsException();
    }

    /* ------------------------------------------------------------ */
    public static boolean contains(Object list, Object item) {
        if (list == null) {
            return false;
        }

        if (list instanceof List) {
            return ((List) list).contains(item);
        }

        return list.equals(item);
    }


    /* ------------------------------------------------------------ */
    public static Object clone(Object list) {
        if (list == null) {
            return null;
        }
        if (list instanceof List) {
            return new ArrayList((List) list);
        }
        return list;
    }

    /* ------------------------------------------------------------ */
    public static String toString(Object list) {
        if (list == null) {
            return "[]";
        }
        if (list instanceof List) {
            return ((List) list).toString();
        }
        return "[" + list + "]";
    }

    /* ------------------------------------------------------------ */
    public static Iterator iterator(Object list) {
        if (list == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        if (list instanceof List) {
            return ((List) list).iterator();
        }
        return getList(list).iterator();
    }

    /* ------------------------------------------------------------ */
    public static ListIterator listIterator(Object list) {
        if (list == null) {
            return Collections.EMPTY_LIST.listIterator();
        }
        if (list instanceof List) {
            return ((List) list).listIterator();
        }
        return getList(list).listIterator();
    }

    /* ------------------------------------------------------------ */
    /**
     * @param array Any array of object
     * @return A new <i>modifiable</i> list initialised with the elements from <code>array</code>.
     */
    public static List array2List(Object[] array) {
        if (array == null || array.length == 0) {
            return new ArrayList();
        }
        return new ArrayList(Arrays.asList(array));
    }

    /* ------------------------------------------------------------ */
    /** Add element to an array
     * @param array The array to add to (or null)
     * @param item The item to add
     * @param type The type of the array (in case of null array)
     * @return new array with contents of array plus item
     */
    public static Object[] addToArray(Object[] array, Object item, Class type) {
        if (array == null) {
            if (type == null && item != null) {
                type = item.getClass();
            }
            Object[] na = (Object[]) Array.newInstance(type, 1);
            na[0] = item;
            return na;
        } else {
            Class c = array.getClass().getComponentType();
            Object[] na = (Object[]) Array.newInstance(c, Array.getLength(array) + 1);
            System.arraycopy(array, 0, na, 0, array.length);
            na[array.length] = item;
            return na;
        }
    }

    /* ------------------------------------------------------------ */
    public static Object[] removeFromArray(Object[] array, Object item) {
        if (item == null || array == null) {
            return array;
        }
        for (int i = array.length; i-- > 0;) {
            if (item.equals(array[i])) {
                Class c = array == null ? item.getClass() : array.getClass().getComponentType();
                Object[] na = (Object[]) Array.newInstance(c, Array.getLength(array) - 1);
                if (i > 0) {
                    System.arraycopy(array, 0, na, 0, i);
                }
                if (i + 1 < array.length) {
                    System.arraycopy(array, i + 1, na, i, array.length - (i + 1));
                }
                return na;
            }
        }
        return array;
    }
}
