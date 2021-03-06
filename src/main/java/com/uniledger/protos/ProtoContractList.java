// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: contract_list.proto

package com.uniledger.protos;

public final class ProtoContractList {
  private ProtoContractList() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ContractListOrBuilder extends
      // @@protoc_insertion_point(interface_extends:protos.ContractList)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    java.util.List<com.uniledger.protos.ProtoContract.Contract> 
        getContractsList();
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    com.uniledger.protos.ProtoContract.Contract getContracts(int index);
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    int getContractsCount();
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    java.util.List<? extends com.uniledger.protos.ProtoContract.ContractOrBuilder> 
        getContractsOrBuilderList();
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    com.uniledger.protos.ProtoContract.ContractOrBuilder getContractsOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code protos.ContractList}
   */
  public  static final class ContractList extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:protos.ContractList)
      ContractListOrBuilder {
    // Use ContractList.newBuilder() to construct.
    private ContractList(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ContractList() {
      contracts_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private ContractList(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                contracts_ = new java.util.ArrayList<com.uniledger.protos.ProtoContract.Contract>();
                mutable_bitField0_ |= 0x00000001;
              }
              contracts_.add(
                  input.readMessage(com.uniledger.protos.ProtoContract.Contract.parser(), extensionRegistry));
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
          contracts_ = java.util.Collections.unmodifiableList(contracts_);
        }
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.uniledger.protos.ProtoContractList.internal_static_protos_ContractList_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.uniledger.protos.ProtoContractList.internal_static_protos_ContractList_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.uniledger.protos.ProtoContractList.ContractList.class, com.uniledger.protos.ProtoContractList.ContractList.Builder.class);
    }

    public static final int CONTRACTS_FIELD_NUMBER = 1;
    private java.util.List<com.uniledger.protos.ProtoContract.Contract> contracts_;
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    public java.util.List<com.uniledger.protos.ProtoContract.Contract> getContractsList() {
      return contracts_;
    }
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    public java.util.List<? extends com.uniledger.protos.ProtoContract.ContractOrBuilder> 
        getContractsOrBuilderList() {
      return contracts_;
    }
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    public int getContractsCount() {
      return contracts_.size();
    }
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    public com.uniledger.protos.ProtoContract.Contract getContracts(int index) {
      return contracts_.get(index);
    }
    /**
     * <pre>
     *for []Contract
     * </pre>
     *
     * <code>repeated .protos.Contract contracts = 1;</code>
     */
    public com.uniledger.protos.ProtoContract.ContractOrBuilder getContractsOrBuilder(
        int index) {
      return contracts_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      for (int i = 0; i < contracts_.size(); i++) {
        output.writeMessage(1, contracts_.get(i));
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      for (int i = 0; i < contracts_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, contracts_.get(i));
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.uniledger.protos.ProtoContractList.ContractList)) {
        return super.equals(obj);
      }
      com.uniledger.protos.ProtoContractList.ContractList other = (com.uniledger.protos.ProtoContractList.ContractList) obj;

      boolean result = true;
      result = result && getContractsList()
          .equals(other.getContractsList());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (getContractsCount() > 0) {
        hash = (37 * hash) + CONTRACTS_FIELD_NUMBER;
        hash = (53 * hash) + getContractsList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.uniledger.protos.ProtoContractList.ContractList parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.uniledger.protos.ProtoContractList.ContractList prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code protos.ContractList}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:protos.ContractList)
        com.uniledger.protos.ProtoContractList.ContractListOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.uniledger.protos.ProtoContractList.internal_static_protos_ContractList_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.uniledger.protos.ProtoContractList.internal_static_protos_ContractList_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.uniledger.protos.ProtoContractList.ContractList.class, com.uniledger.protos.ProtoContractList.ContractList.Builder.class);
      }

      // Construct using com.uniledger.protos.ProtoContractList.ContractList.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getContractsFieldBuilder();
        }
      }
      public Builder clear() {
        super.clear();
        if (contractsBuilder_ == null) {
          contracts_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          contractsBuilder_.clear();
        }
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.uniledger.protos.ProtoContractList.internal_static_protos_ContractList_descriptor;
      }

      public com.uniledger.protos.ProtoContractList.ContractList getDefaultInstanceForType() {
        return com.uniledger.protos.ProtoContractList.ContractList.getDefaultInstance();
      }

      public com.uniledger.protos.ProtoContractList.ContractList build() {
        com.uniledger.protos.ProtoContractList.ContractList result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.uniledger.protos.ProtoContractList.ContractList buildPartial() {
        com.uniledger.protos.ProtoContractList.ContractList result = new com.uniledger.protos.ProtoContractList.ContractList(this);
        int from_bitField0_ = bitField0_;
        if (contractsBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001)) {
            contracts_ = java.util.Collections.unmodifiableList(contracts_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.contracts_ = contracts_;
        } else {
          result.contracts_ = contractsBuilder_.build();
        }
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.uniledger.protos.ProtoContractList.ContractList) {
          return mergeFrom((com.uniledger.protos.ProtoContractList.ContractList)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.uniledger.protos.ProtoContractList.ContractList other) {
        if (other == com.uniledger.protos.ProtoContractList.ContractList.getDefaultInstance()) return this;
        if (contractsBuilder_ == null) {
          if (!other.contracts_.isEmpty()) {
            if (contracts_.isEmpty()) {
              contracts_ = other.contracts_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureContractsIsMutable();
              contracts_.addAll(other.contracts_);
            }
            onChanged();
          }
        } else {
          if (!other.contracts_.isEmpty()) {
            if (contractsBuilder_.isEmpty()) {
              contractsBuilder_.dispose();
              contractsBuilder_ = null;
              contracts_ = other.contracts_;
              bitField0_ = (bitField0_ & ~0x00000001);
              contractsBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getContractsFieldBuilder() : null;
            } else {
              contractsBuilder_.addAllMessages(other.contracts_);
            }
          }
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.uniledger.protos.ProtoContractList.ContractList parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.uniledger.protos.ProtoContractList.ContractList) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<com.uniledger.protos.ProtoContract.Contract> contracts_ =
        java.util.Collections.emptyList();
      private void ensureContractsIsMutable() {
        if (!((bitField0_ & 0x00000001) == 0x00000001)) {
          contracts_ = new java.util.ArrayList<com.uniledger.protos.ProtoContract.Contract>(contracts_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          com.uniledger.protos.ProtoContract.Contract, com.uniledger.protos.ProtoContract.Contract.Builder, com.uniledger.protos.ProtoContract.ContractOrBuilder> contractsBuilder_;

      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public java.util.List<com.uniledger.protos.ProtoContract.Contract> getContractsList() {
        if (contractsBuilder_ == null) {
          return java.util.Collections.unmodifiableList(contracts_);
        } else {
          return contractsBuilder_.getMessageList();
        }
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public int getContractsCount() {
        if (contractsBuilder_ == null) {
          return contracts_.size();
        } else {
          return contractsBuilder_.getCount();
        }
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public com.uniledger.protos.ProtoContract.Contract getContracts(int index) {
        if (contractsBuilder_ == null) {
          return contracts_.get(index);
        } else {
          return contractsBuilder_.getMessage(index);
        }
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder setContracts(
          int index, com.uniledger.protos.ProtoContract.Contract value) {
        if (contractsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureContractsIsMutable();
          contracts_.set(index, value);
          onChanged();
        } else {
          contractsBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder setContracts(
          int index, com.uniledger.protos.ProtoContract.Contract.Builder builderForValue) {
        if (contractsBuilder_ == null) {
          ensureContractsIsMutable();
          contracts_.set(index, builderForValue.build());
          onChanged();
        } else {
          contractsBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder addContracts(com.uniledger.protos.ProtoContract.Contract value) {
        if (contractsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureContractsIsMutable();
          contracts_.add(value);
          onChanged();
        } else {
          contractsBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder addContracts(
          int index, com.uniledger.protos.ProtoContract.Contract value) {
        if (contractsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureContractsIsMutable();
          contracts_.add(index, value);
          onChanged();
        } else {
          contractsBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder addContracts(
          com.uniledger.protos.ProtoContract.Contract.Builder builderForValue) {
        if (contractsBuilder_ == null) {
          ensureContractsIsMutable();
          contracts_.add(builderForValue.build());
          onChanged();
        } else {
          contractsBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder addContracts(
          int index, com.uniledger.protos.ProtoContract.Contract.Builder builderForValue) {
        if (contractsBuilder_ == null) {
          ensureContractsIsMutable();
          contracts_.add(index, builderForValue.build());
          onChanged();
        } else {
          contractsBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder addAllContracts(
          java.lang.Iterable<? extends com.uniledger.protos.ProtoContract.Contract> values) {
        if (contractsBuilder_ == null) {
          ensureContractsIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, contracts_);
          onChanged();
        } else {
          contractsBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder clearContracts() {
        if (contractsBuilder_ == null) {
          contracts_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          contractsBuilder_.clear();
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public Builder removeContracts(int index) {
        if (contractsBuilder_ == null) {
          ensureContractsIsMutable();
          contracts_.remove(index);
          onChanged();
        } else {
          contractsBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public com.uniledger.protos.ProtoContract.Contract.Builder getContractsBuilder(
          int index) {
        return getContractsFieldBuilder().getBuilder(index);
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public com.uniledger.protos.ProtoContract.ContractOrBuilder getContractsOrBuilder(
          int index) {
        if (contractsBuilder_ == null) {
          return contracts_.get(index);  } else {
          return contractsBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public java.util.List<? extends com.uniledger.protos.ProtoContract.ContractOrBuilder> 
           getContractsOrBuilderList() {
        if (contractsBuilder_ != null) {
          return contractsBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(contracts_);
        }
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public com.uniledger.protos.ProtoContract.Contract.Builder addContractsBuilder() {
        return getContractsFieldBuilder().addBuilder(
            com.uniledger.protos.ProtoContract.Contract.getDefaultInstance());
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public com.uniledger.protos.ProtoContract.Contract.Builder addContractsBuilder(
          int index) {
        return getContractsFieldBuilder().addBuilder(
            index, com.uniledger.protos.ProtoContract.Contract.getDefaultInstance());
      }
      /**
       * <pre>
       *for []Contract
       * </pre>
       *
       * <code>repeated .protos.Contract contracts = 1;</code>
       */
      public java.util.List<com.uniledger.protos.ProtoContract.Contract.Builder> 
           getContractsBuilderList() {
        return getContractsFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          com.uniledger.protos.ProtoContract.Contract, com.uniledger.protos.ProtoContract.Contract.Builder, com.uniledger.protos.ProtoContract.ContractOrBuilder> 
          getContractsFieldBuilder() {
        if (contractsBuilder_ == null) {
          contractsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              com.uniledger.protos.ProtoContract.Contract, com.uniledger.protos.ProtoContract.Contract.Builder, com.uniledger.protos.ProtoContract.ContractOrBuilder>(
                  contracts_,
                  ((bitField0_ & 0x00000001) == 0x00000001),
                  getParentForChildren(),
                  isClean());
          contracts_ = null;
        }
        return contractsBuilder_;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:protos.ContractList)
    }

    // @@protoc_insertion_point(class_scope:protos.ContractList)
    private static final com.uniledger.protos.ProtoContractList.ContractList DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.uniledger.protos.ProtoContractList.ContractList();
    }

    public static com.uniledger.protos.ProtoContractList.ContractList getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ContractList>
        PARSER = new com.google.protobuf.AbstractParser<ContractList>() {
      public ContractList parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new ContractList(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ContractList> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ContractList> getParserForType() {
      return PARSER;
    }

    public com.uniledger.protos.ProtoContractList.ContractList getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_protos_ContractList_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_protos_ContractList_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\023contract_list.proto\022\006protos\032\016contract." +
      "proto\"3\n\014ContractList\022#\n\tcontracts\030\001 \003(\013" +
      "2\020.protos.ContractB)\n\024com.uniledger.prot" +
      "osB\021ProtoContractListb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.uniledger.protos.ProtoContract.getDescriptor(),
        }, assigner);
    internal_static_protos_ContractList_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_protos_ContractList_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_protos_ContractList_descriptor,
        new java.lang.String[] { "Contracts", });
    com.uniledger.protos.ProtoContract.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
