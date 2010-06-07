package aors.data.java;

import java.util.EventListener;

public interface CollectionEventListener extends EventListener {

  void collectionEvent(CollectionEvent collectionEvent);

}
