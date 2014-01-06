begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
operator|.
name|KeywordAnalyzer
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|index
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|SegmentInfos
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
name|search
operator|.
name|*
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
name|store
operator|.
name|Directory
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|Version
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
name|Nullable
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|logging
operator|.
name|ESLogger
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
name|analysis
operator|.
name|AnalyzerScope
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|fielddata
operator|.
name|IndexFieldData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Lucene
specifier|public
class|class
name|Lucene
block|{
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|Version
name|VERSION
init|=
name|Version
operator|.
name|LUCENE_46
decl_stmt|;
DECL|field|ANALYZER_VERSION
specifier|public
specifier|static
specifier|final
name|Version
name|ANALYZER_VERSION
init|=
name|VERSION
decl_stmt|;
DECL|field|QUERYPARSER_VERSION
specifier|public
specifier|static
specifier|final
name|Version
name|QUERYPARSER_VERSION
init|=
name|VERSION
decl_stmt|;
DECL|field|STANDARD_ANALYZER
specifier|public
specifier|static
specifier|final
name|NamedAnalyzer
name|STANDARD_ANALYZER
init|=
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_standard"
argument_list|,
name|AnalyzerScope
operator|.
name|GLOBAL
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|ANALYZER_VERSION
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|KEYWORD_ANALYZER
specifier|public
specifier|static
specifier|final
name|NamedAnalyzer
name|KEYWORD_ANALYZER
init|=
operator|new
name|NamedAnalyzer
argument_list|(
literal|"_keyword"
argument_list|,
name|AnalyzerScope
operator|.
name|GLOBAL
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|NO_DOC
specifier|public
specifier|static
specifier|final
name|int
name|NO_DOC
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|EMPTY_SCORE_DOCS
specifier|public
specifier|static
name|ScoreDoc
index|[]
name|EMPTY_SCORE_DOCS
init|=
operator|new
name|ScoreDoc
index|[
literal|0
index|]
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|parseVersion
specifier|public
specifier|static
name|Version
name|parseVersion
parameter_list|(
annotation|@
name|Nullable
name|String
name|version
parameter_list|,
name|Version
name|defaultVersion
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
block|{
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
return|return
name|defaultVersion
return|;
block|}
if|if
condition|(
literal|"4.6"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|VERSION
operator|.
name|LUCENE_46
return|;
block|}
if|if
condition|(
literal|"4.5"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|VERSION
operator|.
name|LUCENE_45
return|;
block|}
if|if
condition|(
literal|"4.4"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|VERSION
operator|.
name|LUCENE_44
return|;
block|}
if|if
condition|(
literal|"4.3"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_43
return|;
block|}
if|if
condition|(
literal|"4.2"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_42
return|;
block|}
if|if
condition|(
literal|"4.1"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_41
return|;
block|}
if|if
condition|(
literal|"4.0"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_40
return|;
block|}
if|if
condition|(
literal|"3.6"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_36
return|;
block|}
if|if
condition|(
literal|"3.5"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_35
return|;
block|}
if|if
condition|(
literal|"3.4"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_34
return|;
block|}
if|if
condition|(
literal|"3.3"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_33
return|;
block|}
if|if
condition|(
literal|"3.2"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_32
return|;
block|}
if|if
condition|(
literal|"3.1"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_31
return|;
block|}
if|if
condition|(
literal|"3.0"
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|LUCENE_30
return|;
block|}
name|logger
operator|.
name|warn
argument_list|(
literal|"no version match {}, default to {}"
argument_list|,
name|version
argument_list|,
name|defaultVersion
argument_list|)
expr_stmt|;
return|return
name|defaultVersion
return|;
block|}
comment|/**      * Reads the segments infos, failing if it fails to load      */
DECL|method|readSegmentInfos
specifier|public
specifier|static
name|SegmentInfos
name|readSegmentInfos
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|directory
argument_list|)
expr_stmt|;
return|return
name|sis
return|;
block|}
DECL|method|count
specifier|public
specifier|static
name|long
name|count
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|TotalHitCountCollector
name|countCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
comment|// we don't need scores, so wrap it in a constant score query
if|if
condition|(
operator|!
operator|(
name|query
operator|instanceof
name|ConstantScoreQuery
operator|)
condition|)
block|{
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|countCollector
argument_list|)
expr_stmt|;
return|return
name|countCollector
operator|.
name|getTotalHits
argument_list|()
return|;
block|}
comment|/**      * Closes the index writer, returning<tt>false</tt> if it failed to close.      */
DECL|method|safeClose
specifier|public
specifier|static
name|boolean
name|safeClose
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|readTopDocs
specifier|public
specifier|static
name|TopDocs
name|readTopDocs
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
comment|// no docs
return|return
literal|null
return|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|totalHits
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|float
name|maxScore
init|=
name|in
operator|.
name|readFloat
argument_list|()
decl_stmt|;
name|SortField
index|[]
name|fields
init|=
operator|new
name|SortField
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|field
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|field
argument_list|,
name|readSortType
argument_list|(
name|in
argument_list|)
argument_list|,
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FieldDoc
index|[]
name|fieldDocs
init|=
operator|new
name|FieldDoc
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Comparable
index|[]
name|cFields
init|=
operator|new
name|Comparable
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|cFields
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|byte
name|type
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|1
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|2
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|3
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|4
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|5
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|6
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|7
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|8
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|9
condition|)
block|{
name|cFields
index|[
name|j
index|]
operator|=
name|in
operator|.
name|readBytesRef
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't match type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
name|fieldDocs
index|[
name|i
index|]
operator|=
operator|new
name|FieldDoc
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|in
operator|.
name|readFloat
argument_list|()
argument_list|,
name|cFields
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TopFieldDocs
argument_list|(
name|totalHits
argument_list|,
name|fieldDocs
argument_list|,
name|fields
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|totalHits
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|float
name|maxScore
init|=
name|in
operator|.
name|readFloat
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|scoreDocs
index|[
name|i
index|]
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|,
name|in
operator|.
name|readFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
block|}
DECL|method|writeTopDocs
specifier|public
specifier|static
name|void
name|writeTopDocs
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|TopDocs
name|topDocs
parameter_list|,
name|int
name|from
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|-
name|from
operator|<
literal|0
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|topDocs
operator|instanceof
name|TopFieldDocs
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TopFieldDocs
name|topFieldDocs
init|=
operator|(
name|TopFieldDocs
operator|)
name|topDocs
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|topFieldDocs
operator|.
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|SortField
name|sortField
range|:
name|topFieldDocs
operator|.
name|fields
control|)
block|{
if|if
condition|(
name|sortField
operator|.
name|getField
argument_list|()
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|sortField
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sortField
operator|.
name|getComparatorSource
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|writeSortType
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|IndexFieldData
operator|.
name|XFieldComparatorSource
operator|)
name|sortField
operator|.
name|getComparatorSource
argument_list|()
operator|)
operator|.
name|reducedType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeSortType
argument_list|(
name|out
argument_list|,
name|sortField
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|sortField
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|-
name|from
argument_list|)
expr_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|topFieldDocs
operator|.
name|scoreDocs
control|)
block|{
if|if
condition|(
name|index
operator|++
operator|<
name|from
condition|)
block|{
continue|continue;
block|}
name|FieldDoc
name|fieldDoc
init|=
operator|(
name|FieldDoc
operator|)
name|doc
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fieldDoc
operator|.
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|field
range|:
name|fieldDoc
operator|.
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Class
name|type
init|=
name|field
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
operator|(
name|String
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
operator|(
name|Integer
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Long
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
operator|(
name|Long
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Float
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
operator|(
name|Float
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Double
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
operator|(
name|Double
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Byte
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|6
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|Byte
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Short
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|7
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
operator|(
name|Short
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|8
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
operator|(
name|Boolean
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|BytesRef
operator|.
name|class
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|9
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesRef
argument_list|(
operator|(
name|BytesRef
operator|)
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't handle sort field value of type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|doc
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|doc
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|-
name|from
argument_list|)
expr_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|topDocs
operator|.
name|scoreDocs
control|)
block|{
if|if
condition|(
name|index
operator|++
operator|<
name|from
condition|)
block|{
continue|continue;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|doc
operator|.
name|doc
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|doc
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// LUCENE 4 UPGRADE: We might want to maintain our own ordinal, instead of Lucene's ordinal
DECL|method|readSortType
specifier|public
specifier|static
name|SortField
operator|.
name|Type
name|readSortType
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SortField
operator|.
name|Type
operator|.
name|values
argument_list|()
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
return|;
block|}
DECL|method|writeSortType
specifier|public
specifier|static
name|void
name|writeSortType
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|SortField
operator|.
name|Type
name|sortType
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|sortType
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readExplanation
specifier|public
specifier|static
name|Explanation
name|readExplanation
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|value
init|=
name|in
operator|.
name|readFloat
argument_list|()
decl_stmt|;
name|String
name|description
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|Explanation
name|explanation
init|=
operator|new
name|Explanation
argument_list|(
name|value
argument_list|,
name|description
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|explanation
operator|.
name|addDetail
argument_list|(
name|readExplanation
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|explanation
return|;
block|}
DECL|method|writeExplanation
specifier|public
specifier|static
name|void
name|writeExplanation
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|Explanation
name|explanation
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeFloat
argument_list|(
name|explanation
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|explanation
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|Explanation
index|[]
name|subExplanations
init|=
name|explanation
operator|.
name|getDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|subExplanations
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|subExplanations
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Explanation
name|subExp
range|:
name|subExplanations
control|)
block|{
name|writeExplanation
argument_list|(
name|out
argument_list|,
name|subExp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|ExistsCollector
specifier|public
specifier|static
class|class
name|ExistsCollector
extends|extends
name|Collector
block|{
DECL|field|exists
specifier|private
name|boolean
name|exists
decl_stmt|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|exists
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|exists
return|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|exists
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|Lucene
specifier|private
name|Lucene
parameter_list|()
block|{      }
DECL|method|indexExists
specifier|public
specifier|static
specifier|final
name|boolean
name|indexExists
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|directory
argument_list|)
return|;
block|}
block|}
end_class

end_unit

