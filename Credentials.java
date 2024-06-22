import java.time.LocalDateTime;

// Clasa Credentials
class Credentials {
    private String email;
    private String password;

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

// Clasa Information
    // Clasa internă Information
    class Information_Credentials {
        // Credențiale private
        private Credentials credentials;

        // Informații personale
        private String nume;
        private String tara;
        private int varsta;
        private char gen; // 'F' pentru feminin, 'M' pentru masculin, 'N' pentru non-binary

        // Data nașterii private
        private LocalDateTime dataNasterii;

        // Constructor privat pentru Builder
        private Information_Credentials(Builder builder) {
            this.credentials = builder.credentials;
            this.nume = builder.nume;
            this.tara = builder.tara;
            this.varsta = builder.varsta;
            this.gen = builder.gen;
            this.dataNasterii = builder.dataNasterii;
        }

        // Getteri pentru a accesa membrii privați
        public Credentials getCredentials() {
            return credentials;
        }

        public String getNume() {
            return nume;
        }

        public String getTara() {
            return tara;
        }

        public int getVarsta() {
            return varsta;
        }

        public char getGen() {
            return gen;
        }

        public LocalDateTime getDataNasterii() {
            return dataNasterii;
        }

        // Clasa Builder pentru Information
        static class Builder {
            // Credențiale private
            private Credentials credentials;

            // Informații personale
            private String nume;
            private String tara;
            private int varsta;
            private char gen; // 'F' pentru feminin, 'M' pentru masculin, 'N' pentru non-binary

            // Data nașterii private
            private LocalDateTime dataNasterii;

            // Setteri pentru a seta valorile în cadrul Builder-ului
            public Builder setCredentials(Credentials credentials) {
                this.credentials = credentials;
                return this;
            }

            public Builder setNume(String nume) {
                this.nume = nume;
                return this;
            }

            public Builder setTara(String tara) {
                this.tara = tara;
                return this;
            }

            public Builder setVarsta(int varsta) {
                this.varsta = varsta;
                return this;
            }

            public Builder setGen(char gen) {
                this.gen = gen;
                return this;
            }

            public Builder setDataNasterii(LocalDateTime dataNasterii) {
                this.dataNasterii = dataNasterii;
                return this;
            }

            // Metodă pentru a construi obiectul Information
            public Information_Credentials build() {
                return new Information_Credentials(this);
            }
        }
    }

