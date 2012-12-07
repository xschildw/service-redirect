package org.sagebase.redirect.filter;

/**
 * The data of a single redirect. 
 * @author jmhill
 *
 */
public class RedirectData {
	
	private int responseCode;
	private String redirectFromHost;
	private String redirectToHost;
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getRedirectFromHost() {
		return redirectFromHost;
	}
	public void setRedirectFromHost(String redirectFromHost) {
		this.redirectFromHost = redirectFromHost;
	}
	public String getRedirectToHost() {
		return redirectToHost;
	}
	public void setRedirectToHost(String redirectToHost) {
		this.redirectToHost = redirectToHost;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((redirectFromHost == null) ? 0 : redirectFromHost.hashCode());
		result = prime * result
				+ ((redirectToHost == null) ? 0 : redirectToHost.hashCode());
		result = prime * result + responseCode;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RedirectData other = (RedirectData) obj;
		if (redirectFromHost == null) {
			if (other.redirectFromHost != null)
				return false;
		} else if (!redirectFromHost.equals(other.redirectFromHost))
			return false;
		if (redirectToHost == null) {
			if (other.redirectToHost != null)
				return false;
		} else if (!redirectToHost.equals(other.redirectToHost))
			return false;
		if (responseCode != other.responseCode)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "RedirectData [responseCode=" + responseCode
				+ ", redirectFromHost=" + redirectFromHost
				+ ", redirectToHost=" + redirectToHost + "]";
	}
	

}
