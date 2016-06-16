package elasticsearchlibrary;

import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseHeaders;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.Response;
import org.asynchttpclient.cookie.Cookie;
import org.asynchttpclient.uri.Uri;

import io.netty.handler.codec.http.HttpHeaders;

public class AppbaseResponse {
	
	public class ResponseBuilder{
		public int hashCode() {
			return builder.hashCode();
		}

		public boolean equals(Object obj) {
			return builder.equals(obj);
		}

		public org.asynchttpclient.Response.ResponseBuilder accumulate(HttpResponseStatus status) {
			return builder.accumulate(status);
		}

		public org.asynchttpclient.Response.ResponseBuilder accumulate(HttpResponseHeaders headers) {
			return builder.accumulate(headers);
		}

		public org.asynchttpclient.Response.ResponseBuilder accumulate(HttpResponseBodyPart bodyPart) {
			return builder.accumulate(bodyPart);
		}

		public Response build() {
			return builder.build();
		}

		public void reset() {
			builder.reset();
		}

		public String toString() {
			return builder.toString();
		}

		org.asynchttpclient.Response.ResponseBuilder builder=new org.asynchttpclient.Response.ResponseBuilder();
	}
	
	
	Response response;
	
	
	public int getStatusCode() {
		return response.getStatusCode();
	}

	public String getStatusText() {
		return response.getStatusText();
	}

	public byte[] getResponseBodyAsBytes() {
		return response.getResponseBodyAsBytes();
	}

	public ByteBuffer getResponseBodyAsByteBuffer() {
		return response.getResponseBodyAsByteBuffer();
	}

	public InputStream getResponseBodyAsStream() {
		return response.getResponseBodyAsStream();
	}

	public String getResponseBody(Charset charset) {
		return response.getResponseBody(charset);
	}

	public String getResponseBody() {
		return response.getResponseBody();
	}

	public Uri getUri() {
		return response.getUri();
	}

	public String getContentType() {
		return response.getContentType();
	}

	public String getHeader(String name) {
		return response.getHeader(name);
	}

	public List<String> getHeaders(String name) {
		return response.getHeaders(name);
	}

	public HttpHeaders getHeaders() {
		return response.getHeaders();
	}

	public boolean isRedirected() {
		return response.isRedirected();
	}

	public String toString() {
		return response.toString();
	}

	public List<Cookie> getCookies() {
		return response.getCookies();
	}

	public boolean hasResponseStatus() {
		return response.hasResponseStatus();
	}

	public boolean hasResponseHeaders() {
		return response.hasResponseHeaders();
	}

	public boolean hasResponseBody() {
		return response.hasResponseBody();
	}

	public SocketAddress getRemoteAddress() {
		return response.getRemoteAddress();
	}

	public SocketAddress getLocalAddress() {
		return response.getLocalAddress();
	}
}
