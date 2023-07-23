package com.ares.core.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * @author wesley
 */

public abstract class UriUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger( UriUtils.class );

	public static List<URI> stringListAsUriList( final List<String> stringList ) {
		return Lists.transform( stringList, new Function<String, URI>() {
			@Override
			public URI apply( String input ) {
				try {
					return URI.create( input );
				} catch ( Exception e ) {
					LOGGER.error( "Exception in URI.create('{}'): {}", input, e );
				}

				return null;
			}
		});

	}
}
