package com.xebialabs.deployit.community.dictionary;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.xebialabs.deployit.plugin.api.udm.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;

public class ComputableDictionary extends Dictionary {

	@Override
	public Map<String, String> getEntries() {
		return getComputedEntries();
	}

	@Override
	public String getValue(String key) {
		return getEntries().get(key);
	}

	@Override
	public boolean hasValue(String key) {
		return getEntries().containsKey(key);
	}

	private Map<String, String> getComputedEntries() {
		final Map<String, String> entries = newHashMap(super.getEntries());
        return computeDictionary(entries);
	}

    protected Map<String, String> computeDictionary(Map<String, String> entries) {
        for (; ; ) {
            final Set<String> previousUnresolvedPlaceholders = getUnresolvedPlaceholders(entries);
            if (previousUnresolvedPlaceholders.isEmpty()) {
                break;
            }
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                try {
                    final String executed = Mustache.compiler().compile(value).execute(entries);
                    entries.put(key, executed);
                } catch (MustacheException me) {
                    //TODO: decide if it should be an error or not !
                    logger.warn("cannot compute '{}' {}", entry, me.getMessage());
                }
            }

            final Set<String> unresolvedPlaceholders = getUnresolvedPlaceholders(entries);
            if (unresolvedPlaceholders.isEmpty()) {
                break;
            }
            if (unresolvedPlaceholders.equals(previousUnresolvedPlaceholders)) {
                logger.warn("no more work can be done with dict but it remains unresolved placeholders: {} ", unresolvedPlaceholders);
                throw new RuntimeException(format("unresolved placeholders %s in %s", unresolvedPlaceholders, getId()));
            }
        }
        return entries;
    }

    private Set<String> getUnresolvedPlaceholders(Map<String, String> entries) {
		Set<String> unresolved = Sets.newHashSet();
		for (String value : entries.values()) {
			unresolved.addAll(scan(value));
		}
		return unresolved;
	}

	private Set<String> scan(String in) {
		Map<String, String> resolution = new MapMaker().makeComputingMap(new Function<String, String>() {
			public String apply(String input) {
				return input;
			}
		});
		Mustache.compiler().compile(in).execute(resolution, new DiscardingWriter());
		return resolution.keySet();

	}

	static class DiscardingWriter extends Writer {
		public void write(char[] cbuf, int off, int len) throws IOException {
		}

		public void flush() throws IOException {
		}

		public void close() throws IOException {
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ComputableDictionary.class);


}