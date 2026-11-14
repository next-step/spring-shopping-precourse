package shopping.domain.util;

import java.util.concurrent.atomic.AtomicLong;

public class IDGenarator {

    private static final AtomicLong id = new AtomicLong(1);

    public static long generate() {
        return id.getAndIncrement();
    }

}
