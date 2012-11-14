begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.index.codec
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|PostingsFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|bloom
operator|.
name|BloomFilteringPostingsFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40PostingsFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectPostingsFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|MemoryPostingsFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pulsing
operator|.
name|Pulsing40PostingsFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|AbstractModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|Scopes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|FactoryProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|multibindings
operator|.
name|MapBinder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
operator|.
name|postingsformat
operator|.
name|PostingsFormatProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
operator|.
name|postingsformat
operator|.
name|PostingsFormatService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
operator|.
name|postingsformat
operator|.
name|PreBuiltPostingsFormatProvider
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|CodecModule
specifier|public
class|class
name|CodecModule
extends|extends
name|AbstractModule
block|{
DECL|field|preConfiguredPostingFormats
specifier|public
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|>
name|preConfiguredPostingFormats
decl_stmt|;
static|static
block|{
name|List
argument_list|<
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|>
name|preConfiguredPostingFormatsX
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|// add defaults ones
for|for
control|(
name|String
name|luceneName
range|:
name|PostingsFormat
operator|.
name|availablePostingsFormats
argument_list|()
control|)
block|{
name|preConfiguredPostingFormatsX
operator|.
name|add
argument_list|(
operator|new
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|(
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|luceneName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|preConfiguredPostingFormatsX
operator|.
name|add
argument_list|(
operator|new
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|(
literal|"direct"
argument_list|,
operator|new
name|DirectPostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|preConfiguredPostingFormatsX
operator|.
name|add
argument_list|(
operator|new
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|(
literal|"memory"
argument_list|,
operator|new
name|MemoryPostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// LUCENE UPGRADE: Need to change this to the relevant ones on a lucene upgrade
name|preConfiguredPostingFormatsX
operator|.
name|add
argument_list|(
operator|new
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|(
literal|"pulsing"
argument_list|,
operator|new
name|Pulsing40PostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|preConfiguredPostingFormatsX
operator|.
name|add
argument_list|(
operator|new
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|(
literal|"bloom_pulsing"
argument_list|,
operator|new
name|BloomFilteringPostingsFormat
argument_list|(
operator|new
name|Pulsing40PostingsFormat
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|preConfiguredPostingFormatsX
operator|.
name|add
argument_list|(
operator|new
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|(
literal|"default"
argument_list|,
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|preConfiguredPostingFormatsX
operator|.
name|add
argument_list|(
operator|new
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
argument_list|(
literal|"bloom_default"
argument_list|,
operator|new
name|BloomFilteringPostingsFormat
argument_list|(
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|preConfiguredPostingFormats
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|preConfiguredPostingFormatsX
argument_list|)
expr_stmt|;
block|}
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|customProviders
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|PostingsFormatProvider
argument_list|>
argument_list|>
name|customProviders
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|CodecModule
specifier|public
name|CodecModule
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
block|}
DECL|method|addPostingFormat
specifier|public
name|CodecModule
name|addPostingFormat
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|PostingsFormatProvider
argument_list|>
name|provider
parameter_list|)
block|{
name|this
operator|.
name|customProviders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|provider
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|PostingsFormatProvider
argument_list|>
argument_list|>
name|postingFormatProviders
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|customProviders
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|postingsFormatsSettings
init|=
name|indexSettings
operator|.
name|getGroups
argument_list|(
literal|"index.codec.postings_format"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|postingsFormatsSettings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|PostingsFormatProvider
argument_list|>
name|type
init|=
name|settings
operator|.
name|getAsClass
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|,
literal|"org.elasticsearch.index.codec.postingsformat."
argument_list|,
literal|"PostingsFormatProvider"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
comment|// nothing found, see if its in bindings as a binding name
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"PostingsFormat Factory ["
operator|+
name|name
operator|+
literal|"] must have a type associated with it"
argument_list|)
throw|;
block|}
name|postingFormatProviders
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|// now bind
name|MapBinder
argument_list|<
name|String
argument_list|,
name|PostingsFormatProvider
operator|.
name|Factory
argument_list|>
name|postingFormatFactoryBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|PostingsFormatProvider
operator|.
name|Factory
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|PostingsFormatProvider
argument_list|>
argument_list|>
name|entry
range|:
name|postingFormatProviders
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|postingFormatFactoryBinder
operator|.
name|addBinding
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|PostingsFormatProvider
operator|.
name|Factory
operator|.
name|class
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PreBuiltPostingsFormatProvider
operator|.
name|Factory
name|factory
range|:
name|preConfiguredPostingFormats
control|)
block|{
if|if
condition|(
name|postingFormatProviders
operator|.
name|containsKey
argument_list|(
name|factory
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|postingFormatFactoryBinder
operator|.
name|addBinding
argument_list|(
name|factory
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|toInstance
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
name|bind
argument_list|(
name|PostingsFormatService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|CodecService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

