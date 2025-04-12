package simplf; 

public class AssocList {
        final String name;
        Object value;
        final AssocList next;
    
        AssocList(String nameIn, Object valueIn, AssocList nextIn) {
            name = nameIn;
            value = valueIn;
            next = nextIn;
        }
    
}

