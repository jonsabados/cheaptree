/**
 * Makes calls to the API - basically like calling window.fetch but instead of a URL the first argument is the API
 * resource to hit. Additionally adds credentials: 'include' to the args so cookies and whatnot get included (api
 * resources will all be on a api sub domain)
 *
 * @param {string} resource the API resource to hit - should be just the path including leading slash (ie /helloworld)
 * @param {RequestInit} args arguments to pass to fetch
 * @return {Promise} returned promise is the same as the returned promise from window.fetch
 */
function apiFetch(resource, args) {
  if (!args) {
    args = {};
  }
  args.credentials = "include";
  const hostname = window.location.hostname;
  const protocol = window.location.protocol;
  const url = protocol + "//api." + hostname + resource;
  return fetch(url, args);
}

export default apiFetch;
