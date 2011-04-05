package togos.mf.api;

/** An object that can handle requests using any of the various call styles.
 * Generally, an implementation of this will implement one primary call style,
 * and implement all others in terms of the primary one.  For example, a handler
 * that is concerned only with sending off a request and ignoring any return
 * values would probably implement all other call styles by calling send(). */
public interface RequestHandler extends SendHandler, CallHandler, OpenSessionHandler, OpenSyncHandler, OpenAsyncHandler {}